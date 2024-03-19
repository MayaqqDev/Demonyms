package dev.mayaqq.demonyms.registry;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import dev.mayaqq.demonyms.registry.screens.ChooseDemonymScreen;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class DemonymsCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            LiteralCommandNode<ServerCommandSource> demonyms = CommandManager.literal("demonyms").executes(context -> {
                return ChooseDemonymScreen.create(context.getSource().getPlayer());
            }).build();

            RootCommandNode<ServerCommandSource> root = dispatcher.getRoot();
            root.addChild(demonyms);
        });
    }
}
