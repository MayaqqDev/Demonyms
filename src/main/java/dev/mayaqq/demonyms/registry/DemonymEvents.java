package dev.mayaqq.demonyms.registry;

import dev.mayaqq.demonyms.registry.screens.ChooseDemonymScreen;
import dev.mayaqq.demonyms.storage.DemonymsState;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class DemonymEvents {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity entity = handler.player;
            DemonymsState.PlayerState playerState = DemonymsState.getPlayerState(entity);
            if (playerState.firstJoin) {
                playerState.firstJoin = false;
                ChooseDemonymScreen.create(entity);
            }
        });
    }
}
