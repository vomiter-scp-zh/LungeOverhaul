package com.vomiter.lungeoverhaul.common.event;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vomiter.lungeoverhaul.common.LungeMode;
import com.vomiter.lungeoverhaul.common.LungeAttachments;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.command.EnumArgument;

public final class LungeCommands {
    private LungeCommands() {}

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("lungeoverhaul")
                        .executes(context -> {
                            LungeMode mode = LungeAttachments.get(context.getSource().getPlayerOrException());
                            context.getSource().sendSuccess(() -> Component.translatable("message.lungeoverhaul.player_default", mode.displayName()), false);
                            return 1;
                        })
                .then(Commands.literal("default")
                        .then(Commands.argument("mode", EnumArgument.enumArgument(LungeMode.Arg.class))
                                .executes(context -> {
                                    LungeMode mode = LungeMode.valueOf(context.getArgument("mode", LungeMode.Arg.class).toString());
                                    if (mode == LungeMode.PLAYER_DEFAULT) {
                                        context.getSource().sendFailure(Component.literal("Player Default can't be set to Player Default."));
                                        return 0;
                                    }
                                    LungeAttachments.set(context.getSource().getPlayerOrException(), mode);
                                    context.getSource().sendSuccess(() -> Component.translatable("message.lungeoverhaul.player_default", mode.displayName()), false);
                                    return 1;
                                }))));
    }
}
