package dev.mayaqq.demonyms.registry.screens;

import dev.mayaqq.demonyms.resources.Demonym;
import dev.mayaqq.demonyms.resources.DemonymProcessor;
import dev.mayaqq.demonyms.storage.DemonymsState;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChooseDemonymScreen {
    public static int create(ServerPlayerEntity player) {
        DemonymsState.PlayerState playerState = DemonymsState.getPlayerState(player);
        AtomicBoolean shouldClose = new AtomicBoolean(false);
        SimpleGui gui = new SimpleGui(DemonymProcessor.DEMONYMS.size() <= 7 ? ScreenHandlerType.GENERIC_9X3 : ScreenHandlerType.GENERIC_9X6, player, false) {
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

        Set<Map.Entry<Identifier, Demonym>> demonyms = DemonymProcessor.DEMONYMS.entrySet();

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
                        confirmDemonym(player, entry.getKey());
                        shouldClose.set(true);
                        gui.close();
                    }
            );
        }

        gui.open();
        return 0;
    }

    private static void confirmDemonym(ServerPlayerEntity player, Identifier demonym) {
        DemonymsState.PlayerState playerState = DemonymsState.getPlayerState(player);
        playerState.demonym = demonym;

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);

        gui.setTitle(Text.translatable("gui.demonyms.choose_demonym.confirm_title"));

        makeFullBackground(gui);

        gui.setSlot(11, new GuiElementBuilder()
                .setItem(Items.GREEN_STAINED_GLASS_PANE)
                .setName(Text.translatable("gui.demonyms.choose_demonym.confirm")).asStack(),
                (index, type, action) -> {
                    playerState.demonym = demonym;
                    player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1, 1);
                    gui.close();
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
                    .setName(Text.of(" "))
            );
        }
    }
}
