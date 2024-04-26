package dev.mayaqq.demonyms.registry;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import dev.mayaqq.demonyms.registry.commands.DemonymsCommand;
import dev.mayaqq.demonyms.registry.screens.ChooseDemonymScreen;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class DemonymsCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            LiteralCommandNode<ServerCommandSource> demonyms = CommandManager.literal("demonyms").build();
            LiteralCommandNode<ServerCommandSource> choose = CommandManager.literal("choose").executes(ChooseDemonymScreen::create).build();
            LiteralCommandNode<ServerCommandSource> fetch = CommandManager.literal("fetch").executes(DemonymsCommand::fetchMyself).build();
            ArgumentCommandNode<ServerCommandSource, EntitySelector> fetchPlayer = CommandManager.argument("player", EntityArgumentType.player()).executes(DemonymsCommand::fetchPlayer).build();

            RootCommandNode<ServerCommandSource> root = dispatcher.getRoot();
            fetch.addChild(fetchPlayer);
            demonyms.addChild(fetch);
            demonyms.addChild(choose);
            root.addChild(demonyms);
        });
    }
}
