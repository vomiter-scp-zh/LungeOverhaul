package com.vomiter.lungeoverhaul.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class LungeModes {

    public static LungeMode stored(ItemStack stack) {
        String id = stack.getOrDefault(LungeComponents.STACK_MODE.get(), LungeMode.PLAYER_DEFAULT.id());
        return LungeMode.byId(id).orElse(LungeMode.PLAYER_DEFAULT);
    }

    public static LungeMode resolved(Player player, ItemStack stack) {
        LungeMode mode = stored(stack);
        return mode == LungeMode.PLAYER_DEFAULT ? LungeAttachments.get(player) : mode;
    }

    public static void set(ItemStack stack, LungeMode mode) {
        stack.set(LungeComponents.STACK_MODE.get(), mode.id());
    }
}
