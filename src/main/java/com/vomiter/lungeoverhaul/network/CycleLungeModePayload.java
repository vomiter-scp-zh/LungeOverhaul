package com.vomiter.lungeoverhaul.network;

import com.vomiter.lungeoverhaul.LungeOverhaul;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record CycleLungeModePayload() implements CustomPacketPayload {
    public static final Type<@NotNull CycleLungeModePayload> TYPE = new Type<>(LungeOverhaul.id("cycle_mode"));
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull CycleLungeModePayload> STREAM_CODEC =
            StreamCodec.unit(new CycleLungeModePayload());

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
