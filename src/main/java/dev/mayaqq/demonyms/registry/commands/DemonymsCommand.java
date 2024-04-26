package dev.mayaqq.demonyms.registry.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.mayaqq.demonyms.Attachments.DemonymPlayer;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class DemonymsCommand {
    public static int fetchPlayer(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity source = context.getSource().getPlayer();
            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
            source.sendMessage(Text.of( player.getStyledDisplayName().getString() + "s demonym is " + DemonymPlayer.get(player).demonym.id().getPath() + "."), false);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public static int fetchMyself(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity source = context.getSource().getPlayer();
        source.sendMessage(Text.of("Your demonym is " + DemonymPlayer.get(source).demonym.id().getPath() + "."), false);
        return 1;
    }
}
