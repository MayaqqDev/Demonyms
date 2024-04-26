package dev.mayaqq.demonyms.Attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mayaqq.demonyms.resources.Demonym;
import net.minecraft.server.network.ServerPlayerEntity;

public class DemonymPlayer {
    public boolean firstJoin;
    public Demonym demonym;
    public static final Codec<DemonymPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("firstJoin").forGetter(DemonymPlayer::isFirstJoin),
            Demonym.CODEC.fieldOf("demonym").forGetter(DemonymPlayer::getDemonym)
    ).apply(instance, DemonymPlayer::new));

    public DemonymPlayer(Boolean firstJoin, Demonym demonym) {
        this.firstJoin = firstJoin;
        this.demonym = demonym;
    }

    public DemonymPlayer(ServerPlayerEntity player) {}

    public boolean isFirstJoin() {
        return firstJoin;
    }

    public Demonym getDemonym() {
        return demonym;
    }

    public void setDemonym(Demonym demonym) {
        this.demonym = demonym;
    }

    public void setFirstJoin(Boolean firstJoin) {
        this.firstJoin = firstJoin;
    }

    public static DemonymPlayer get(ServerPlayerEntity player) {
        return player.getAttachedOrCreate(DemonymAttachments.SERVER_PLAYER, () -> new DemonymPlayer(player));
    }
}
