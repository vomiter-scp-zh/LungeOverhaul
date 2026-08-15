package com.vomiter.lungeoverhaul.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vomiter.lungeoverhaul.common.LungeThreadLocals;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @WrapMethod(method = "doPostAttackEffects")
    private static void lungeoverhaul$doPostPiercingAttackEffects(ServerLevel serverLevel, Entity user, DamageSource damageSource, Operation<Void> original){
        try{
            if (user instanceof LivingEntity livingEntity){
                LungeThreadLocals.isDoingPostPiercing.set(true);
                LungeThreadLocals.spearUsingEntity.set(livingEntity);
            }
            original.call(serverLevel, user, damageSource);
        } finally {
            LungeThreadLocals.isDoingPostPiercing.remove();
            LungeThreadLocals.spearUsingEntity.remove();
        }

    }

    @WrapOperation(method = "runIterationOnItem(" +
            "Lnet/minecraft/world/item/ItemStack;" +
            "Lnet/minecraft/world/entity/EquipmentSlot;" +
            "Lnet/minecraft/world/entity/LivingEntity;" +
            "Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;" +
            ")V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;accept(Lnet/minecraft/core/Holder;ILnet/minecraft/world/item/enchantment/EnchantedItemInUse;)V"))
    private static void lungeoverhaul$runIterationOnItem(
            EnchantmentHelper.EnchantmentInSlotVisitor instance,
            Holder<@NotNull Enchantment> enchantmentHolder,
            int i,
            EnchantedItemInUse enchantedItemInUse,
            Operation<Void> original){
        try{
            if (enchantmentHolder.is(Enchantments.LUNGE)){
                LungeThreadLocals.isDoingLunge.set(true);
                LungeThreadLocals.spear.set(enchantedItemInUse);
            }
            original.call(instance, enchantmentHolder, i, enchantedItemInUse);
        } finally {
            LungeThreadLocals.isDoingLunge.remove();
            LungeThreadLocals.spear.remove();
        }
    }
}
