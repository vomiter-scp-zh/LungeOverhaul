package com.vomiter.lungeoverhaul.common.event;

import com.vomiter.lungeoverhaul.LungeConfig;
import com.vomiter.lungeoverhaul.common.LungeModes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class LungeEvents {
    private static final Set<UUID> ACTIVE_CHARGES = new HashSet<>();
    private static final Map<UUID, Integer> CHARGE_RECOVERY_TICKS = new HashMap<>();

    /**
     * Total number of ticks during which movement is forcibly stopped after
     * ending a charge.
     */
    private static final int RECOVERY_TICKS = 10;

    private LungeEvents() {
    }

    public static void tickCharge(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !(player instanceof ServerPlayer)) {
            return;
        }

        UUID playerId = player.getUUID();

        if (applyChargeRecovery(player)) {
            return;
        }

        ItemStack stack = player.getUseItem();
        if (!canCharge(player, stack)) {
            if (ACTIVE_CHARGES.remove(playerId)) {
                stopPlayer(player);
                player.stopUsingItem();

                /*
                 * The current tick is already the first recovery tick, so only
                 * store the remaining ticks.
                 */
                if (RECOVERY_TICKS > 1) {
                    CHARGE_RECOVERY_TICKS.put(
                            playerId,
                            RECOVERY_TICKS - 1
                    );
                }
            }

            return;
        }

        HolderLookup.RegistryLookup<@NotNull Enchantment> lookup =
                CommonHooks.resolveLookup(Registries.ENCHANTMENT);
        assert lookup != null;

        int lungeLevel = lookup.get(Enchantments.LUNGE)
                .map(enchantmentReference ->
                        EnchantmentHelper.getTagEnchantmentLevel(
                                enchantmentReference,
                                stack
                        )
                )
                .orElse(0);

        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);

        if (horizontal.lengthSqr() < 1.0E-6D) {
            return;
        }

        horizontal = horizontal.normalize();

        Vec3 velocity = player.getDeltaMovement();

        if (ACTIVE_CHARGES.add(playerId)) {
            velocity = velocity.add(horizontal.scale(
                    LungeConfig.INITIAL_SPEED.getAsDouble() * lungeLevel
            ));

            player.causeFoodExhaustion(4.0F * lungeLevel);
        }

        double forward =
                velocity.x * horizontal.x
                        + velocity.z * horizontal.z;

        double allowed = Math.max(
                0.0D,
                LungeConfig.MAX_HORIZONTAL_SPEED.getAsDouble()
                        * lungeLevel
                        - forward
        );

        double acceleration = Math.min(
                allowed,
                LungeConfig.TICK_ACCELERATION.getAsDouble() * lungeLevel
        );

        player.setDeltaMovement(
                velocity.add(horizontal.scale(acceleration))
        );
        player.hurtMarked = true;

        player.causeFoodExhaustion(
                (float) LungeConfig.TICK_EXHAUSTION.getAsDouble()
                        * lungeLevel
        );
    }

    public static void chargeHitExhaustion(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (event.getSource().getDirectEntity() != player) {
            return;
        }

        if (ACTIVE_CHARGES.contains(player.getUUID())) {
            player.causeFoodExhaustion(
                    (float) LungeConfig.HIT_EXHAUSTION.getAsDouble()
            );
        }
    }

    public static void clearChargeState(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();

        ACTIVE_CHARGES.remove(playerId);
        CHARGE_RECOVERY_TICKS.remove(playerId);
    }

    private static boolean applyChargeRecovery(Player player) {
        UUID playerId = player.getUUID();
        Integer remainingTicks = CHARGE_RECOVERY_TICKS.get(player.getUUID());
        if (remainingTicks == null) {
            return false;
        }

        stopPlayer(player);

        if (remainingTicks <= 1) {
            CHARGE_RECOVERY_TICKS.remove(playerId);
        } else {
            CHARGE_RECOVERY_TICKS.put(playerId, remainingTicks - 1);
        }

        return true;
    }

    private static void stopPlayer(Player player) {
        player.setDeltaMovement(Vec3.ZERO);
        player.hurtMarked = true;
    }

    private static boolean canCharge(Player player, ItemStack stack) {
        Integer remainingTicks = CHARGE_RECOVERY_TICKS.get(player.getUUID());
        if(remainingTicks != null) return false;


        if (!player.isUsingItem()
                || !stack.is(ItemTags.SPEARS)) {
            return false;
        }

        KineticWeapon kineticWeapon =
                stack.get(DataComponents.KINETIC_WEAPON);

        if (kineticWeapon == null) {
            return false;
        }

        if (player.isPassenger()
                || player.isFallFlying()
                || player.isInWater()) {
            return false;
        }

        if (player.getFoodData().getFoodLevel()
                < LungeConfig.MIN_FOOD_LEVEL.getAsInt()) {
            return false;
        }

        if (!LungeModes.resolved(player, stack).enablesCharge()) {
            return false;
        }

        Holder<Enchantment> lunge = player.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.LUNGE);

        if (stack.getEnchantmentLevel(lunge) <= 0) {
            return false;
        }

        int ticksAfterDelay =
                player.getTicksUsingItem() - kineticWeapon.delayTicks();

        /*
         * Vanilla KineticWeapon subtracts delayTicks before evaluating its
         * conditions. Knockback ending marks the transition into Disengaged.
         *
         * Negative values represent the initial preparation period, which is
         * still allowed to receive acceleration here.
         */
        int knockbackDuration = kineticWeapon.knockbackConditions()
                .map(KineticWeapon.Condition::maxDurationTicks)
                .orElse(-1);

        return ticksAfterDelay <= knockbackDuration;
    }
}