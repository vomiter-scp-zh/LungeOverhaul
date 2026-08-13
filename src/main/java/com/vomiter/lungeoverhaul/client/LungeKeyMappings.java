package com.vomiter.lungeoverhaul.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.vomiter.lungeoverhaul.LungeOverhaul;
import com.vomiter.lungeoverhaul.network.CycleLungeModePayload;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = LungeOverhaul.MOD_ID, value = Dist.CLIENT)
public final class LungeKeyMappings {
    private static final KeyMapping.Category CATEGORY = new KeyMapping.Category(LungeOverhaul.id("main"));
    private static final KeyMapping CYCLE_MODE = new KeyMapping(
            "key.lungeoverhaul.cycle_mode",
            KeyConflictContext.IN_GAME,
            KeyModifier.CONTROL,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            CATEGORY);

    private LungeKeyMappings() {}

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(CYCLE_MODE);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (CYCLE_MODE.consumeClick()) {
            ClientPacketDistributor.sendToServer(new CycleLungeModePayload());
        }
    }
}
