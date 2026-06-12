package gbz.complementary.client.hud

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import kotlin.math.PI
import kotlin.math.atan2

/**
 * Replaces the vanilla white cooldown wash with a "pizza" sweep drawn on top
 * of the item. Invoked from DrawContextMixin in place of
 * DrawContext.drawCooldownProgress.
 *
 * The pie is rasterized into horizontal runs and drawn with the same
 * fill()/getGuiOverlay() path vanilla uses for its own cooldown overlay, so it
 * renders anywhere the vanilla overlay would (hotbar and container screens).
 */
object ItemCooldownOverlayRenderer {
    // The pie is rasterized on a finer grid than the 16x16 slot and drawn
    // under a scaled matrix, so the circle edge stays smooth.
    private const val SIZE = 128
    private const val SLOT_SIZE = 16f
    private const val RADIUS = SIZE / 2f

    private val PIE_COLOR = 0x99D0D0D0L.toInt()

    // Brightness multiplier applied to the item while it is cooling down.
    private const val DIM_BRIGHTNESS = 0.5f

    private data class PendingPie(val stack: ItemStack, val x: Int, val y: Int, val progress: Float)

    private val pending = ArrayList<PendingPie>()

    private var currentSlotIsPlayerInventory = false

    /** Set by HandledScreenMixin around each slot draw. */
    fun setCurrentSlotIsPlayerInventory(isPlayerInventory: Boolean) {
        currentSlotIsPlayerInventory = isPlayerInventory
    }

    fun register() {
        HudLayerRegistrationCallback.EVENT.register { layeredDrawer ->
            layeredDrawer.attachLayerAfter(IdentifiedLayer.SUBTITLES, Identifier.of("gbz", "item_cooldown_overlay")) { drawContext, _ ->
                for (pie in pending) {
                    drawPie(drawContext, pie.stack, pie.x, pie.y, pie.progress)
                }
                pending.clear()
            }
        }
    }

    /** Called from DrawContextMixin in place of vanilla drawCooldownProgress. */
    fun render(drawContext: DrawContext, stack: ItemStack, x: Int, y: Int) {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return
        val tickDelta = client.renderTickCounter.getTickDelta(true)
        val progress = player.itemCooldownManager.getCooldownProgress(stack, tickDelta)
        if (progress <= 0f) return

        if (client.currentScreen == null) {
            // Hotbar items are drawn after this hook runs, which would put the
            // pie behind the item; defer it to a HUD layer rendered afterwards.
            pending.add(PendingPie(stack, x, y, progress))
        } else if (currentSlotIsPlayerInventory) {
            // Only show cooldowns on the player's own slots (set per-slot by
            // HandledScreenMixin), not on chest/container slots.
            drawPie(drawContext, stack, x, y, progress)
        }
    }

    private fun drawPie(drawContext: DrawContext, stack: ItemStack, x: Int, y: Int, progress: Float) {
        val sweep = progress * 2.0 * PI

        // Redraw the item darkened over the original instead of washing the
        // whole slot square, so the dim follows the item sprite. Same z as the
        // original item: the depth test is "lequal", so it still wins, and the
        // depth we write stays below tooltips (z 400).
        drawContext.matrices.push()
        RenderSystem.setShaderColor(DIM_BRIGHTNESS, DIM_BRIGHTNESS, DIM_BRIGHTNESS, 1f)
        drawContext.drawItem(stack, x, y)
        // Item geometry goes through entity render layers with dedicated
        // buffers that are only drawn on the final batch flush — after our
        // fills, regardless of call order. Flush now so the pie lands on top.
        drawContext.draw()
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        drawContext.matrices.pop()

        drawContext.matrices.push()
        // z 200: same level vanilla uses for stack counts — above the item
        // model but below tooltips.
        drawContext.matrices.translate(x.toFloat(), y.toFloat(), 200f)
        val scale = SLOT_SIZE / SIZE
        drawContext.matrices.scale(scale, scale, 1f)

        for (py in 0 until SIZE) {
            var inPie = false
            var runStart = 0
            for (px in 0 until SIZE) {
                val state = isInPie(px, py, sweep)
                if (state != inPie) {
                    if (inPie) emitRun(drawContext, py, runStart, px)
                    inPie = state
                    runStart = px
                }
            }
            if (inPie) emitRun(drawContext, py, runStart, SIZE)
        }

        drawContext.matrices.pop()

        // The pie covers the vanilla stack count, so draw it again on top,
        // at the same position vanilla uses.
        if (stack.count > 1) {
            val textRenderer = MinecraftClient.getInstance().textRenderer
            val label = stack.count.toString()
            drawContext.matrices.push()
            // Same z vanilla uses for the stack count.
            drawContext.matrices.translate(0f, 0f, 200f)
            drawContext.drawText(textRenderer, label, x + 19 - 2 - textRenderer.getWidth(label), y + 6 + 3, 0xFFFFFFFF.toInt(), true)
            drawContext.matrices.pop()
        }

        // Flush now; otherwise the pie sits in the batch until the end of the
        // frame and gets drawn over tooltips rendered after this slot.
        drawContext.draw()
    }

    private fun emitRun(drawContext: DrawContext, py: Int, fromPx: Int, toPx: Int) {
        if (fromPx >= toPx) return
        drawContext.fill(RenderLayer.getGuiOverlay(), fromPx, py, toPx, py + 1, PIE_COLOR)
    }

    private fun isInPie(px: Int, py: Int, sweep: Double): Boolean {
        val dx = px + 0.5 - SIZE / 2f
        val dy = py + 0.5 - SIZE / 2f
        if (dx * dx + dy * dy > RADIUS * RADIUS) return false

        // Angle measured from the top of the circle, increasing clockwise.
        var angle = atan2(dx.toDouble(), -dy.toDouble())
        if (angle < 0) angle += 2.0 * PI
        return angle <= sweep
    }
}
