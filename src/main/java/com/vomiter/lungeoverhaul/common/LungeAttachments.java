package com.vomiter.lungeoverhaul.common;

import com.mojang.serialization.Codec;
import com.vomiter.lungeoverhaul.LungeOverhaul;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class LungeAttachments {
    public static final DeferredRegister<@NotNull AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, LungeOverhaul.MOD_ID);
    public static final Supplier<AttachmentType<@NotNull String>> DEFAULT_MODE = ATTACHMENTS.register("default_mode", () ->
            AttachmentType.builder(LungeMode.JAB_ONLY::id)
                    .serialize(Codec.STRING)
                    .copyOnDeath()
                    .build());

    public static LungeMode get(Player player) {
        return LungeMode.byId(player.getData(DEFAULT_MODE)).orElse(LungeMode.JAB_ONLY);
    }

    public static void set(Player player, LungeMode mode) {
        player.setData(DEFAULT_MODE, mode.id());
    }
}
