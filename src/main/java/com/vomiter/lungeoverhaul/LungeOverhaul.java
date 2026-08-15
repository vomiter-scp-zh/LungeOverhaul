package com.vomiter.lungeoverhaul;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.notunanancyowen.spears.components.KineticWeapon;
import com.vomiter.lungeoverhaul.common.LungeComponents;
import com.vomiter.lungeoverhaul.common.event.LungeEvents;
import com.vomiter.lungeoverhaul.common.event.LungeCommands;
import com.vomiter.lungeoverhaul.common.LungeAttachments;
import com.vomiter.lungeoverhaul.network.LungePayloads;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(LungeOverhaul.MOD_ID)
public final class LungeOverhaul {
    public static final String MOD_ID = "lungeoverhaul";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static Supplier<DataComponentType<KineticWeapon>> KINETIC_WEAPON;

    public LungeOverhaul(IEventBus modBus, ModContainer modContainer) {
        LungeComponents.COMPONENTS.register(modBus);
        LungeAttachments.ATTACHMENTS.register(modBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, LungeConfig.SPEC);
        modBus.addListener(LungePayloads::register);

        NeoForge.EVENT_BUS.addListener(LungeEvents::tickCharge);
        NeoForge.EVENT_BUS.addListener(LungeEvents::chargeHitExhaustion);
        NeoForge.EVENT_BUS.addListener(LungeEvents::clearChargeState);
        NeoForge.EVENT_BUS.addListener(LungeCommands::register);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
