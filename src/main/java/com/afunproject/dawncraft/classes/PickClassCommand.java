package com.afunproject.dawncraft.classes;

import com.afunproject.dawncraft.classes.event.PickClassEvent;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;

public class PickClassCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("pickClass")
                .requires((commandSource) -> commandSource.hasPermission(1))
                .then(Commands.argument("player", EntityArgument.players())
                        .then(Commands.argument("class", ResourceLocationArgument.id()).suggests(ClassHandler::getSuggestions).executes(PickClassCommand::execute)
                        .then(Commands.argument("add_items", BoolArgumentType.bool()).executes(PickClassCommand::execute))));
        dispatcher.register(command);
    }

    public static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        boolean addItems = true;
        try {
            addItems = BoolArgumentType.getBool(ctx, "add_items");
        } catch (Exception e) {}
        for (ServerPlayer player : EntityArgument.getPlayers(ctx, "player")) {
            LazyOptional<PickedClass> optional = player.getCapability(DCClasses.PICKED_CLASS, null);
            try {
                if (optional.isPresent()) {
                    PickedClass cap = optional.resolve().get();
                    ResourceLocation clazz = ResourceLocationArgument.getId(ctx, "class");
                    PickClassEvent event = new PickClassEvent.Pre(player, ClassHandler.getClass(clazz), true);
                    MinecraftForge.EVENT_BUS.post(event);
                    cap.setDCClass(event.getDCClass());
                    if (cap.hasEffect()) cap.applyEffect(player, addItems);
                    else cap.applyEffect(player, true);
                    MinecraftForge.EVENT_BUS.post(new PickClassEvent.Post(player, event.getDCClass(), true));
                    ClassesLogger.logInfo("Successfully ran command to add " + player + " to class " + clazz);
                }
            } catch (Exception e) {
                ClassesLogger.logError("Failed to run pick class command", e);
            }
        }
        return 1;
    }

}
