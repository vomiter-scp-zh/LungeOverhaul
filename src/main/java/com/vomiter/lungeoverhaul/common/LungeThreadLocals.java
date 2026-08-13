package com.vomiter.lungeoverhaul.common;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;

public class LungeThreadLocals {
    public static ThreadLocal<Boolean> isDoingPostPiercing = ThreadLocal.withInitial(() -> false);
    public static ThreadLocal<Boolean> isDoingLunge = ThreadLocal.withInitial(() -> false);
    public static ThreadLocal<LivingEntity> spearUsingEntity = ThreadLocal.withInitial(() -> null);
    public static ThreadLocal<EnchantedItemInUse> spear = ThreadLocal.withInitial(() -> new EnchantedItemInUse(ItemStack.EMPTY, null, null, _ ->{}));

}
