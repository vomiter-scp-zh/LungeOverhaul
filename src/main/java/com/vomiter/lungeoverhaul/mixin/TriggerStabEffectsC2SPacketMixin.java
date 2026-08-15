package com.vomiter.lungeoverhaul.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.notunanancyowen.spears.packets.TriggerStabEffectsC2SPacket;
import com.vomiter.lungeoverhaul.LungeOverhaul;
import com.vomiter.lungeoverhaul.common.LungeModes;
import com.vomiter.lungeoverhaul.common.LungeThreadLocals;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TriggerStabEffectsC2SPacket.class, remap = false)
public class TriggerStabEffectsC2SPacketMixin {

    @WrapMethod(method = "trigger")
    private void lungeoverhaul$doPostPiercingAttackEffects(ServerPlayer me, Operation<Void> original){
        try{
            if (me instanceof LivingEntity livingEntity){
                LungeThreadLocals.isDoingPostPiercing.set(true);
                LungeThreadLocals.spearUsingEntity.set(livingEntity);
            }
            original.call(me);
        } finally {
            LungeThreadLocals.isDoingPostPiercing.remove();
            LungeThreadLocals.spearUsingEntity.remove();
        }

    }

    @WrapOperation(method = "trigger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/effects/EnchantmentEntityEffect;apply(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/item/enchantment/EnchantedItemInUse;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;)V"))
    private void lungeoverhaul$runIterationOnItem(
            EnchantmentEntityEffect instance,
            ServerLevel serverLevel,
            int i,
            EnchantedItemInUse enchantedItemInUse,
            Entity entity,
            Vec3 vec3,
            Operation<Void> original,
            @Local(name = "registryEntry") Holder<Enchantment> registryEntry){
        try{
            LungeOverhaul.LOGGER.info("ENCHANTMENT = {}", registryEntry.getKey().location());
            if (registryEntry.is(ResourceLocation.fromNamespaceAndPath("minecraft", "lunge"))){
                if(
                    entity instanceof Player player
                    && !LungeModes.resolved(player, player.getMainHandItem()).enablesJab()
                ){
                    return;
                }
            }
            original.call(instance, serverLevel, i, enchantedItemInUse, entity, vec3);
        } finally {
        }
    }
}
