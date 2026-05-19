package gbz.complementary.client.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Inject(method = "drawCooldownProgress", at = @At("HEAD"), cancellable = true)
    private void gbz$hideVanillaCooldown(ItemStack stack, int x, int y, CallbackInfo ci) {
        ci.cancel();
    }
}
