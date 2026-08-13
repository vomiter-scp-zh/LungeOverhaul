package com.vomiter.lungeoverhaul.common;

import com.mojang.serialization.Codec;
import com.vomiter.lungeoverhaul.LungeOverhaul;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class LungeComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LungeOverhaul.MOD_ID);
    public static final DeferredHolder<@NotNull DataComponentType<?>, @NotNull DataComponentType<@NotNull String>> STACK_MODE =
            COMPONENTS.registerComponentType("mode", builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8));

}
