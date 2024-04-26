package dev.mayaqq.demonyms.registry.screens;

import com.mojang.brigadier.context.CommandContext;
import dev.mayaqq.demonyms.Attachments.DemonymPlayer;
import dev.mayaqq.demonyms.Demonyms;
import dev.mayaqq.demonyms.resources.Demonym;
import dev.mayaqq.demonyms.resources.DemonymsProcessor;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.entity.attribute.*;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChooseDemonymScreen {

    public static final UUID DemonymModifierUUID = UUID.fromString("a1732122-e22e-4edf-883c-09673eb55de8");

    public static int create(CommandContext<ServerCommandSource> context) {
        return create(context.getSource().getPlayer());
    }

    public static int create(ServerPlayerEntity player) {
        AtomicBoolean shouldClose = new AtomicBoolean(false);
        SimpleGui gui = new SimpleGui(DemonymsProcessor.DEMONYMS.size() <= 7 ? ScreenHandlerType.GENERIC_9X3 : ScreenHandlerType.GENERIC_9X6, player, false) {
            @Override
            public void close(boolean screenHandlerIsClosed) {
                if (shouldClose.get()) {
                    super.close(screenHandlerIsClosed);
                } else {
                    create(player);
                }
            }
        };

        gui.setTitle(Text.translatable("gui.demonyms.choose_demonym.title"));

        makeBackground(gui);

        Set<Map.Entry<Identifier, Demonym>> demonyms = DemonymsProcessor.DEMONYMS.entrySet();

        for (int i = 0; i < demonyms.size(); i++) {
            Map.Entry<Identifier, Demonym> entry = (Map.Entry<Identifier, Demonym>) demonyms.toArray()[i];
            gui.setSlot(i + 10, new GuiElementBuilder()
                    .setItem(entry.getValue().item().asItem())
                    // Translatable text for the demonym name, e.g. "demonym.examplemod.exampledemonym.name"
                    .setName(Text.translatable("demonym." + entry.getKey().getNamespace() + "." + entry.getKey().getPath() + ".name"))
                    .setLore(
                        List.of(
                            // Translatable text for the demonym description, e.g. "demonym.examplemod.exampledemonym.description"
                            Text.translatable("demonym." + entry.getKey().getNamespace() + "." + entry.getKey().getPath() + ".description"),
                            Text.of(" "),
                            Text.translatable("gui.demonyms.choose_demonym.description")
                        )
                    )
                    .asStack(),
                    (index, type, action) -> {
                        confirmDemonym(player, entry.getValue());
                        shouldClose.set(true);
                        gui.close();
                    }
            );
        }

        gui.open();
        return 0;
    }

    private static void confirmDemonym(ServerPlayerEntity player, Demonym demonym) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);

        gui.setTitle(Text.translatable("gui.demonyms.choose_demonym.confirm_title"));

        makeFullBackground(gui);

        gui.setSlot(11, new GuiElementBuilder()
                .setItem(Items.GREEN_STAINED_GLASS_PANE)
                .setName(Text.translatable("gui.demonyms.choose_demonym.confirm")).asStack(),
                (index, type, action) -> {
                        if (removeDemonym(player, Demonyms.getPlayerDemonym(player))) {
                            setDemonym(player, demonym);
                            player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1, 1);
                            gui.close();
                        }
                }
        );

        gui.setSlot(15, new GuiElementBuilder()
                .setItem(Items.RED_STAINED_GLASS_PANE)
                .setName(Text.translatable("gui.demonyms.choose_demonym.cancel")).asStack(),
                (index, type, action) -> {
                    gui.close();
                    create(player);
                }
        );

        gui.open();
    }

    public static void makeBackground(SimpleGui gui) {
        makeFullBackground(gui);

        for (int i = 10; i < gui.getSize() - 10; i++) {
            gui.setSlot(i, Items.AIR.getDefaultStack());
        }
    }

    public static void makeFullBackground(SimpleGui gui) {
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setSlot(i, new GuiElementBuilder()
                    .setItem(Items.GRAY_STAINED_GLASS_PANE)
                    .hideTooltip()
            );
        }
    }

    public static void setDemonym(ServerPlayerEntity player, Demonym demonym) {
        DemonymPlayer.get(player).setDemonym(demonym);
        AttributeContainer attributeContainer = player.getAttributes();

        if (demonym.attributes() == null) return;

        demonym.attributes().forEach((id, value) -> {
            EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance(Registries.ATTRIBUTE.getEntry(Registries.ATTRIBUTE.get(id)));
            if (entityAttributeInstance != null) {
                AttributeModifierCreator modifierCreator = new AttributeModifierCreator(DemonymModifierUUID, value, EntityAttributeModifier.Operation.ADD_VALUE, demonym.id().toString());
                entityAttributeInstance.removeModifier(modifierCreator.getUuid());
                entityAttributeInstance.addPersistentModifier(modifierCreator.createAttributeModifier(0));
            }
        });
    }

    public static boolean removeDemonym(ServerPlayerEntity player, Demonym demonym) {
        if (demonym.attributes() == null) return true;

        demonym.attributes().keySet().forEach(id -> {
            EntityAttribute attribute = Registries.ATTRIBUTE.get(id);
            if (attribute != null) {
                player.getAttributes().getCustomInstance(Registries.ATTRIBUTE.getEntry(attribute)).removeModifier(DemonymModifierUUID);
            }
        });
        return true;
    }

    record AttributeModifierCreator(UUID uuid, double baseValue, EntityAttributeModifier.Operation operation, String name) {
        public UUID getUuid() {
            return this.uuid;
        }

        public EntityAttributeModifier createAttributeModifier(int amplifier) {
            return new EntityAttributeModifier(this.uuid,  this.name, this.baseValue, this.operation);
        }
    }
}
