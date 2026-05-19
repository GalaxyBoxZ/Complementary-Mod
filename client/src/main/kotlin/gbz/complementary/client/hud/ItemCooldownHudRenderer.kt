package gbz.complementary.client.hud

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper
import kotlin.math.abs
import kotlin.math.roundToInt

object ItemCooldownHudRenderer {
    private var trackedStack: ItemStack = ItemStack.EMPTY
    private var displayedProgress = 0f
    private var displayedAlpha = 0f

    fun register() {
        HudRenderCallback.EVENT.register { drawContext, _ ->
            render(drawContext)
        }
    }

    private fun render(drawContext: DrawContext) {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return
        if (client.options.hudHidden) return

        val cooldownManager = player.itemCooldownManager
        val activeStack = findTrackedStack(player)
        val targetProgress = if (activeStack.isEmpty) 0f else cooldownManager.getCooldownProgress(activeStack, 0f)

        if (targetProgress <= 0f) {
            trackedStack = ItemStack.EMPTY
        } else if (trackedStack.isEmpty || !ItemStack.areItemsAndComponentsEqual(trackedStack, activeStack)) {
            trackedStack = activeStack.copy()
        }

        displayedProgress = smooth(displayedProgress, targetProgress, if (targetProgress < displayedProgress) 0.45f else 0.22f)
        displayedAlpha = smooth(displayedAlpha, if (targetProgress > 0f) 1f else 0f, 0.25f)

        if (displayedAlpha <= 0.02f || trackedStack.isEmpty) return

        val width = drawContext.scaledWindowWidth
        val height = drawContext.scaledWindowHeight
        val frameX = (width / 2) - (BAR_WIDTH / 2)
        val frameY = height - 54
        val alpha = (displayedAlpha * 255f).roundToInt().coerceIn(0, 255)
        val iconX = frameX - 20
        val iconY = frameY - 1

        drawContext.drawItem(trackedStack, iconX, iconY)

        renderFrame(drawContext, frameX, frameY, alpha)
        renderFill(drawContext, frameX, frameY, displayedProgress, alpha)
        renderReadyEdge(drawContext, frameX, frameY, displayedProgress, alpha)

        val textRenderer = client.textRenderer
        val label = trackedStack.name.string.uppercase()
        val percent = "${(displayedProgress * 100f).roundToInt()}%"
        drawContext.drawText(textRenderer, label, frameX, frameY - 10, withAlpha(0xE8F7F9, alpha), true)
        val percentWidth = textRenderer.getWidth(percent)
        drawContext.drawText(
            textRenderer,
            percent,
            frameX + BAR_WIDTH - percentWidth,
            frameY - 10,
            withAlpha(0xE8F7F9, alpha),
            true
        )
    }

    private fun findTrackedStack(player: net.minecraft.entity.player.PlayerEntity): ItemStack {
        val cooldownManager = player.itemCooldownManager
        val mainHand = player.mainHandStack
        if (!mainHand.isEmpty && cooldownManager.isCoolingDown(mainHand)) {
            return mainHand
        }

        val offHand = player.offHandStack
        if (!offHand.isEmpty && cooldownManager.isCoolingDown(offHand)) {
            return offHand
        }

        if (!trackedStack.isEmpty && cooldownManager.isCoolingDown(trackedStack)) {
            return trackedStack
        }

        for (slot in 0 until 9) {
            val stack = player.inventory.getStack(slot)
            if (!stack.isEmpty && cooldownManager.isCoolingDown(stack)) {
                return stack
            }
        }

        return ItemStack.EMPTY
    }

    private fun renderFrame(drawContext: DrawContext, x: Int, y: Int, alpha: Int) {
        drawContext.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, withAlpha(0x2A494F, alpha))
        drawContext.fill(x + 1, y + 1, x + BAR_WIDTH - 1, y + BAR_HEIGHT - 1, withAlpha(0x0B171A, ((alpha * 0.86f).roundToInt())))
        drawContext.fill(x + 1, y + 1, x + BAR_WIDTH - 1, y + 3, withAlpha(0x7DE8F0, ((alpha * 0.2f).roundToInt())))
    }

    private fun renderFill(drawContext: DrawContext, x: Int, y: Int, progress: Float, alpha: Int) {
        val clamped = progress.coerceIn(0f, 1f)
        val fillWidth = ((BAR_WIDTH - 2) * clamped).roundToInt()
        if (fillWidth <= 0) return

        val color = when {
            clamped >= 0.85f -> 0x5DE5FF
            clamped >= 0.45f -> 0x46B8D3
            else -> 0x2F728E
        }

        drawContext.fill(x + 1, y + 1, x + 1 + fillWidth, y + BAR_HEIGHT - 1, withAlpha(color, alpha))
    }

    private fun renderReadyEdge(drawContext: DrawContext, x: Int, y: Int, progress: Float, alpha: Int) {
        if (progress <= 0f) return

        val edgeX = x + 1 + ((BAR_WIDTH - 2) * progress.coerceIn(0f, 1f)).roundToInt()
        drawContext.fill(
            (edgeX - 1).coerceAtLeast(x + 1),
            y + 1,
            edgeX.coerceAtMost(x + BAR_WIDTH - 1),
            y + BAR_HEIGHT - 1,
            withAlpha(0xD4FBFF, ((alpha * 0.9f).roundToInt()))
        )
    }

    private fun smooth(current: Float, target: Float, factor: Float): Float {
        val next = MathHelper.lerp(factor, current, target)
        return if (abs(next - target) < 0.01f) target else next
    }

    private fun withAlpha(rgb: Int, alpha: Int): Int {
        return ((alpha.coerceIn(0, 255) and 0xFF) shl 24) or (rgb and 0xFFFFFF)
    }

    private const val BAR_WIDTH = 92
    private const val BAR_HEIGHT = 8
}
