package com.vomiter.lungeoverhaul.network;

import com.vomiter.lungeoverhaul.common.LungeMode;
import com.vomiter.lungeoverhaul.common.LungeModes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class LungePayloads {
    private LungePayloads() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(CycleLungeModePayload.TYPE, CycleLungeModePayload.STREAM_CODEC,
                LungePayloads::handleCycle);
    }

    private static void handleCycle(CycleLungeModePayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            cycle(player);
        }
    }

    private static void cycle(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("minecraft", "spears")))) {
            return;
        }
        LungeMode next = LungeModes.stored(stack).next();
        LungeModes.set(stack, next);
        player.displayClientMessage(Component.translatable("message.lungeoverhaul.lunge_mode", next.displayName()), true);
    }
}
