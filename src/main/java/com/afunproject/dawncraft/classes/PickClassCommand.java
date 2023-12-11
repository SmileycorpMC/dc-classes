package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.data.DCClassLoader;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Collection;

public class PickClassCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("pickClass")
                .requires((commandSource) -> commandSource.hasPermission(1))
                .then(Commands.argument("player", EntityArgument.players())
                        .then(Commands.argument("class", ResourceLocationArgument.id()).suggests(ClassHandler::getSuggestions).executes(PickClassCommand::execute)));
        dispatcher.register(command);
    }

    public static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        for (Player player : EntityArgument.getPlayers(ctx, "player")) {
            LazyOptional<PickedClass> optional = player.getCapability(DCClasses.PICKED_CLASS, null);
            try {
                if (optional.isPresent()) {
                    ResourceLocation clazz = ResourceLocationArgument.getId(ctx, "class");
                    optional.resolve().get().setDCClass(ClassHandler.getClass(clazz));
                }
            } catch (Exception e) {
                ClassesLogger.logError("Failed to run pick class command", e);
            }
        }
        return 1;
    }

}
