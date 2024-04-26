package dev.mayaqq.demonyms.registry;

import dev.mayaqq.demonyms.Attachments.DemonymPlayer;
import dev.mayaqq.demonyms.registry.screens.ChooseDemonymScreen;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class DemonymsEvents {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity entity = handler.player;
            if (DemonymPlayer.get(entity).firstJoin) {
                DemonymPlayer.get(entity).setFirstJoin(false);
                ChooseDemonymScreen.create(entity);
            }
        });
    }
}
