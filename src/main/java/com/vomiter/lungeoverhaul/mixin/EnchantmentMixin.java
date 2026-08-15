package com.vomiter.lungeoverhaul.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vomiter.lungeoverhaul.common.LungeModes;
import com.vomiter.lungeoverhaul.common.LungeThreadLocals;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.List;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @WrapOperation(method = "doPostAttack(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/item/enchantment/EnchantedItemInUse;Lnet/minecraft/world/item/enchantment/EnchantmentTarget;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/Enchantment;doPostAttack(Lnet/minecraft/world/item/enchantment/TargetedConditionalEffect;Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/item/enchantment/EnchantedItemInUse;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void lungeoverhaul$doPostPiercingAttack(
            TargetedConditionalEffect<EnchantmentEntityEffect> effect,
            ServerLevel level,
            int enchantmentLevel,
            EnchantedItemInUse item,
            Entity p_entity,
            DamageSource damageSource,
            Operation<Void> original
    ){
        if(
                LungeThreadLocals.isDoingLunge.get()
                && LungeThreadLocals.isDoingPostPiercing.get()
                && LungeThreadLocals.spearUsingEntity.get() instanceof Player player
                && !LungeModes.resolved(player, LungeThreadLocals.spear.get().itemStack()).enablesJab()
        ){
            return;
        }
        original.call(effect, level, enchantmentLevel, item, p_entity, damageSource);
    }
}
