package gbz.complementary.client.mixin;

import gbz.complementary.client.hud.CombatBarsHudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void gbz$renderHealthBar(
        DrawContext drawContext,
        PlayerEntity player,
        int x,
        int y,
        int lines,
        int regeneratingHeartIndex,
        float maxHealth,
        int lastHealth,
        int health,
        int absorption,
        boolean blinking,
        CallbackInfo ci
    ) {
        CombatBarsHudRenderer.INSTANCE.renderHealthBar(drawContext, player, x, y);
        ci.cancel();
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void gbz$renderFood(DrawContext drawContext, PlayerEntity player, int x, int y, CallbackInfo ci) {
        CombatBarsHudRenderer.INSTANCE.renderStaminaBar(drawContext, player, x, y);
        ci.cancel();
    }
}
