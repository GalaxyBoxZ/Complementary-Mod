package gbz.complementary.client.util

import gbz.complementary.client.animation.CombatAnimationManager
import gbz.complementary.client.matcher.WeaponAnimationResolver
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.math.roundToInt

class DebugOverlayRenderer(
    private val animationManager: CombatAnimationManager,
    private val resolver: WeaponAnimationResolver
) {
    fun register() {
        HudRenderCallback.EVENT.register { drawContext, _ ->
            render(drawContext)
        }
    }

    private fun render(drawContext: DrawContext) {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return
        val state = animationManager.currentState()
        if (!state.debugOverlayEnabled) return

        val textRenderer = client.textRenderer
        val lines = arrayOf(
            "GBZ",
            "Weapon type: ${state.currentType.id}",
            "Model key: ${state.modelKey}",
            "Rule: ${state.matchedRule ?: "<default>"} (${state.matchPriority})",
            "Attack speed: ${format(state.lastAttackSpeed)}",
            "Cooldown ticks: ${format(state.lastCooldownTicks)}",
            "Playback speed: ${format(state.lastPlaybackSpeed)}",
            "Resolver cache: ${resolver.cacheSize()}",
            "Held stack empty: ${player.mainHandStack.isEmpty}"
        )

        var y = 6
        for (line in lines) {
            drawContext.drawText(textRenderer, Text.literal(line), 6, y, 0xFFFFFF, true)
            y += 10
        }
    }

    private fun format(value: Float): String = ((value * 100f).roundToInt() / 100f).toString()
}
