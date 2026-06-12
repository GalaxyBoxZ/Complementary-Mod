package gbz.complementary.client.mixin;

import gbz.complementary.client.hud.ItemCooldownOverlayRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void gbz$markSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        ItemCooldownOverlayRenderer.INSTANCE.setCurrentSlotIsPlayerInventory(slot.inventory instanceof PlayerInventory);
    }

    @Inject(method = "drawSlot", at = @At("RETURN"))
    private void gbz$clearSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        ItemCooldownOverlayRenderer.INSTANCE.setCurrentSlotIsPlayerInventory(false);
    }

    // drawItem renders the cursor-held (or touch-dragged) stack, which always
    // belongs to the player, so cooldowns should show on it too.
    @Inject(method = "drawItem", at = @At("HEAD"))
    private void gbz$markCursorItem(DrawContext context, ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
        ItemCooldownOverlayRenderer.INSTANCE.setCurrentSlotIsPlayerInventory(true);
    }

    @Inject(method = "drawItem", at = @At("RETURN"))
    private void gbz$clearCursorItem(DrawContext context, ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
        ItemCooldownOverlayRenderer.INSTANCE.setCurrentSlotIsPlayerInventory(false);
    }
}
