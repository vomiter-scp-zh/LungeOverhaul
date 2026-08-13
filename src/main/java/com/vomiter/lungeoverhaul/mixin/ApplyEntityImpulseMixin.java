package com.vomiter.lungeoverhaul.mixin;

import com.vomiter.lungeoverhaul.common.LungeModes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.ApplyEntityImpulse;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ApplyEntityImpulse.class)
public class ApplyEntityImpulseMixin {
    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void lungeoverhaul$blockJabImpulse(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item,
                                                Entity entity, Vec3 position, CallbackInfo ci) {
        if (entity instanceof Player player
                && item.itemStack().is(ItemTags.SPEARS)
                && !LungeModes.resolved(player, item.itemStack()).enablesJab()) {
            ci.cancel();
        }
    }
}
