package gbz.complementary.client.hud

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object ItemCooldownHudRenderer {
    private var trackedStack: ItemStack = ItemStack.EMPTY
    private var displayedProgress = 0f
    private var displayedAlpha = 0f

    fun register() {
        HudLayerRegistrationCallback.EVENT.register { layeredDrawer ->
            layeredDrawer.attachLayerAfter(IdentifiedLayer.SUBTITLES, Identifier.of("gbz", "item_cooldown_hud")) { drawContext, _ ->
                render(drawContext)
            }
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

        val screenWidth = drawContext.scaledWindowWidth
        val screenHeight = drawContext.scaledWindowHeight
        val cx = screenWidth / 2f
        val cy = screenHeight - 56f
        val alpha = (displayedAlpha * 255f).roundToInt().coerceIn(0, 255)

        val iconX = (cx - RADIUS - 22).toInt()
        val iconY = (cy - 8).toInt()
        drawContext.drawItem(trackedStack, iconX, iconY)

        // Outer ring
        drawFilledArc(drawContext, cx, cy, (RADIUS + 2).toFloat(), 1f, withAlpha(0x2A494F, alpha))
        // Dark background fill
        drawFilledArc(drawContext, cx, cy, (RADIUS + 1).toFloat(), 1f, withAlpha(0x0B171A, (alpha * 0.86f).roundToInt()))

        // Pie sector showing remaining cooldown
        if (displayedProgress > 0f) {
            val fillColor = when {
                displayedProgress >= 0.85f -> 0x5DE5FF
                displayedProgress >= 0.45f -> 0x46B8D3
                else -> 0x2F728E
            }
            drawFilledArc(drawContext, cx, cy, RADIUS.toFloat(), displayedProgress, withAlpha(fillColor, alpha))
        }

        val textRenderer = client.textRenderer
        val label = trackedStack.name.string.uppercase()
        val percent = "${(displayedProgress * 100f).roundToInt()}%"
        val textY = (cy - RADIUS - 10).toInt()
        val textStartX = (cx - RADIUS - 20).toInt()
        val textEndX = (cx + RADIUS).toInt()
        drawContext.drawText(textRenderer, label, textStartX, textY, withAlpha(0xE8F7F9, alpha), true)
        val percentWidth = textRenderer.getWidth(percent)
        drawContext.drawText(textRenderer, percent, textEndX - percentWidth, textY, withAlpha(0xE8F7F9, alpha), true)
    }

    private fun findTrackedStack(player: net.minecraft.entity.player.PlayerEntity): ItemStack {
        val cooldownManager = player.itemCooldownManager
        val mainHand = player.mainHandStack
        if (!mainHand.isEmpty && cooldownManager.isCoolingDown(mainHand)) return mainHand
        val offHand = player.offHandStack
        if (!offHand.isEmpty && cooldownManager.isCoolingDown(offHand)) return offHand
        if (!trackedStack.isEmpty && cooldownManager.isCoolingDown(trackedStack)) return trackedStack
        for (slot in 0 until 9) {
            val stack = player.inventory.getStack(slot)
            if (!stack.isEmpty && cooldownManager.isCoolingDown(stack)) return stack
        }
        return ItemStack.EMPTY
    }

    private fun drawFilledArc(drawContext: DrawContext, cx: Float, cy: Float, radius: Float, progress: Float, color: Int) {
        val a = (color ushr 24) and 0xFF
        val r = (color ushr 16) and 0xFF
        val g = (color ushr 8) and 0xFF
        val b = color and 0xFF

        if (a == 0 || progress <= 0f) return

        val clamped = progress.coerceIn(0f, 1f)
        val startAngle = -PI / 2
        val sweepAngle = 2.0 * PI * clamped
        val segments = (64 * clamped).roundToInt().coerceAtLeast(1)

        val matrix = drawContext.matrices.peek().positionMatrix

        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR)

        buffer.vertex(matrix, cx, cy, 0f).color(r, g, b, a)
        for (i in 0..segments) {
            val angle = startAngle + sweepAngle * i.toDouble() / segments
            val vx = cx + radius * cos(angle).toFloat()
            val vy = cy + radius * sin(angle).toFloat()
            buffer.vertex(matrix, vx, vy, 0f).color(r, g, b, a)
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end())
        RenderSystem.disableBlend()
    }

    private fun smooth(current: Float, target: Float, factor: Float): Float {
        val next = MathHelper.lerp(factor, current, target)
        return if (abs(next - target) < 0.01f) target else next
    }

    private fun withAlpha(rgb: Int, alpha: Int): Int {
        return ((alpha.coerceIn(0, 255) and 0xFF) shl 24) or (rgb and 0xFFFFFF)
    }

    private const val RADIUS = 16
}
