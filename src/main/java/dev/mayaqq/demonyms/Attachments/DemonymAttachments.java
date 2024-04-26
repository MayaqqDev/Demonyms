package dev.mayaqq.demonyms.Attachments;

import dev.mayaqq.demonyms.Demonyms;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class DemonymAttachments {
    public static final AttachmentType<DemonymPlayer> SERVER_PLAYER = AttachmentRegistry.<DemonymPlayer>builder()
            .persistent(DemonymPlayer.CODEC)
            .copyOnDeath()
            .buildAndRegister(Demonyms.id("server_player"));

    public static void register() {}
}
