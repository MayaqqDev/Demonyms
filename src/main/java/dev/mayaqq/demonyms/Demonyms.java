package dev.mayaqq.demonyms;

import dev.mayaqq.demonyms.registry.DemonymsEvents;
import dev.mayaqq.demonyms.registry.DemonymsCommands;
import dev.mayaqq.demonyms.resources.Demonym;
import dev.mayaqq.demonyms.resources.DemonymsProcessor;
import dev.mayaqq.demonyms.storage.DemonymsState;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Demonyms implements ModInitializer {

    public static final String MODID = "demonyms";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        DemonymsProcessor.register();
        DemonymsEvents.register();
        DemonymsCommands.register();
    }

    public static Demonym getPlayerDemonym(PlayerEntity player) {
        return DemonymsProcessor.DEMONYMS.get(DemonymsState.getPlayerState(player).demonym);
    }

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }
}
