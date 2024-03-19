package dev.mayaqq.demonyms.mixin;

import dev.mayaqq.demonyms.resources.DemonymsProcessor;
import dev.mayaqq.demonyms.storage.DemonymsState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Shadow
    @Final
    public PlayerEntity player;

    @Inject(at = @At(value = "HEAD"), method = "updateItems")
    private void demonyms$updateItems(CallbackInfo ci) {
        ArrayList<ItemStack> handStacks = new ArrayList<>();
        handStacks.add(player.getStackInHand(Hand.MAIN_HAND));
        handStacks.add(player.getStackInHand(Hand.OFF_HAND));
        try {
            if (DemonymsProcessor.DEMONYMS.get(DemonymsState.getPlayerState(player).demonym).disallowedItems() == null) return;
        } catch (NullPointerException e) {
            return;
        }
        for (ItemStack item : handStacks) {
            if (item.isIn(DemonymsProcessor.DEMONYMS.get(DemonymsState.getPlayerState(player).demonym).disallowedItems())) {
                item.decrement(item.getCount());
                player.dropStack(item);
            }
        }
    }
}
