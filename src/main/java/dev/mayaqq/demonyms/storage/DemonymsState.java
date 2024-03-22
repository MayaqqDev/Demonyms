package dev.mayaqq.demonyms.storage;

import dev.mayaqq.demonyms.Demonyms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class DemonymsState extends PersistentState {

    public HashMap<UUID, PlayerState> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // Putting the 'players' hashmap, into the 'nbt' which will be saved.
        NbtCompound playersNbtCompound = new NbtCompound();
        players.forEach((UUID, playerSate) -> {
            NbtCompound playerStateNbt = new NbtCompound();

            playerStateNbt.putString("demonym", playerSate.demonym.toString());
            playerStateNbt.putBoolean("firstJoin", playerSate.firstJoin);

            playersNbtCompound.put(String.valueOf(UUID), playerStateNbt);
        });

        nbt.put("players", playersNbtCompound);
        return nbt;
    }

    public static DemonymsState createFromNbt(NbtCompound tag) {
        DemonymsState serverState = new DemonymsState();
        NbtCompound playersTag = tag.getCompound("players");
        playersTag.getKeys().forEach(key -> {
            PlayerState playerState = new PlayerState();

            playerState.demonym = new Identifier(playersTag.getCompound(key).getString("demonym"));
            playerState.firstJoin = playersTag.getCompound(key).getBoolean("firstJoin");

            UUID uuid = UUID.fromString(key);
            serverState.players.put(uuid, playerState);
        });
        return serverState;
    }

    private static final Type<DemonymsState> type = new Type<>(
            DemonymsState::new,
            DemonymsState::createFromNbt,
            null
    );

    public static DemonymsState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        DemonymsState serverState = persistentStateManager.getOrCreate(type, Demonyms.MODID);

        serverState.markDirty(); // makes stuff work

        return serverState;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public static PlayerState getPlayerState(LivingEntity player) {
        DemonymsState serverState = getServerState(player.getServer());
        serverState.markDirty();
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerState());
    }
    public static class PlayerState {
        public boolean firstJoin = true;
        public Identifier demonym = Demonyms.id("default");
    }
}
