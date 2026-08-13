package com.vomiter.lungeoverhaul.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vomiter.lungeoverhaul.common.LungeModes;
import com.vomiter.lungeoverhaul.common.LungeThreadLocals;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.List;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @WrapOperation(method = "doPostPiercingAttack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/Enchantment;" +
                            "applyEffects(Ljava/util/List;Lnet/minecraft/world/level/storage/loot/LootContext;" +
                            "Lnet/minecraft/world/item/enchantment/Enchantment$GenericAction;)V"))
    private void lungeoverhaul$doPostPiercingAttack(
            List<ConditionalEffect<@NotNull Object>> effects,
            LootContext filterData,
            @Coerce Object action,
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
        original.call(effects, filterData, action);
    }
}
