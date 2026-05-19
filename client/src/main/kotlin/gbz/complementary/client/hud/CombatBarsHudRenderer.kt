package gbz.complementary.client.hud

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.MathHelper
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin

object CombatBarsHudRenderer {
    private var displayedHealth = -1f
    private var trailingHealth = -1f
    private var displayedStamina = -1f
    private var lastHealthTarget = -1f
    private var lastDamageAt = 0L

    fun renderHealthBar(drawContext: DrawContext, player: PlayerEntity, x: Int, y: Int) {
        if (!shouldRenderBars(player)) return

        val currentHealth = player.health.coerceAtLeast(0f)
        val maxHealth = max(player.maxHealth, 1f)
        val absorption = player.absorptionAmount.coerceAtLeast(0f)

        updateHealthState(currentHealth)

        val pulse = lowHealthPulse(currentHealth / maxHealth)
        val fillColor = when {
            currentHealth / maxHealth <= 0.25f -> interpolateColor(0xFF8E2A2A.toInt(), 0xFFE14B4B.toInt(), pulse)
            currentHealth / maxHealth <= 0.5f -> 0xFFD1842F.toInt()
            else -> 0xFF2FA864.toInt()
        }

        renderBarFrame(drawContext, x, y, 0xAA120E0E.toInt(), 0xFF3F2A2A.toInt())
        renderChip(drawContext, x, y, trailingHealth / maxHealth, 0xA9B53E3E.toInt())
        renderFill(drawContext, x, y, displayedHealth / maxHealth, fillColor)

        if (absorption > 0f) {
            renderAbsorption(drawContext, x, y, currentHealth, maxHealth, absorption)
        }

        renderGloss(drawContext, x, y)
        renderCenteredText(
            drawContext = drawContext,
            x = x,
            y = y,
            text = "HP ${formatValue(currentHealth)} / ${formatValue(maxHealth)}",
            color = 0xFFFFFFFF.toInt()
        )
    }

    fun renderStaminaBar(drawContext: DrawContext, player: PlayerEntity, x: Int, y: Int) {
        if (!shouldRenderBars(player)) return

        val hungerManager = player.hungerManager
        val stamina = hungerManager.foodLevel.toFloat().coerceAtLeast(0f)
        val maxStamina = 20f
        val saturation = hungerManager.saturationLevel.coerceAtLeast(0f)

        displayedStamina = smooth(displayedStamina, stamina, if (stamina < displayedStamina) 0.35f else 0.18f)

        val leftX = x - BAR_WIDTH + 1
        val lowPulse = lowHealthPulse(stamina / maxStamina)
        val fillColor = if (stamina <= 6f) {
            interpolateColor(0xFF6A8F1F.toInt(), 0xFFC8A632.toInt(), lowPulse)
        } else {
            0xFF3AA8B9.toInt()
        }

        renderBarFrame(drawContext, leftX, y, 0xAA0C1418.toInt(), 0xFF28434B.toInt())
        renderFill(drawContext, leftX, y, displayedStamina / maxStamina, fillColor)

        if (saturation > 0f) {
            val saturationWidth = ((BAR_INNER_WIDTH * (displayedStamina / maxStamina)) * (saturation / maxStamina)).roundToInt()
                .coerceIn(0, BAR_INNER_WIDTH)
            if (saturationWidth > 0) {
                val innerX = leftX + BAR_BORDER
                val innerY = y + BAR_BORDER
                drawContext.fill(
                    innerX,
                    innerY,
                    innerX + saturationWidth,
                    innerY + BAR_INNER_HEIGHT,
                    0x55BDE8F0
                )
            }
        }

        renderGloss(drawContext, leftX, y)
        renderCenteredText(
            drawContext = drawContext,
            x = leftX,
            y = y,
            text = "STA ${formatValue(stamina)} / ${formatValue(maxStamina)}",
            color = 0xFFFFFFFF.toInt()
        )
    }

    private fun updateHealthState(targetHealth: Float) {
        if (displayedHealth < 0f) {
            displayedHealth = targetHealth
            trailingHealth = targetHealth
            lastHealthTarget = targetHealth
            return
        }

        if (targetHealth < lastHealthTarget) {
            lastDamageAt = System.currentTimeMillis()
        }

        displayedHealth = smooth(displayedHealth, targetHealth, if (targetHealth < displayedHealth) 0.35f else 0.16f)

        trailingHealth = when {
            targetHealth >= trailingHealth -> targetHealth
            System.currentTimeMillis() - lastDamageAt < DAMAGE_TRAIL_DELAY_MS -> trailingHealth
            else -> smooth(trailingHealth, targetHealth, 0.14f)
        }

        lastHealthTarget = targetHealth
    }

    private fun renderBarFrame(drawContext: DrawContext, x: Int, y: Int, backgroundColor: Int, borderColor: Int) {
        drawContext.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, borderColor)
        drawContext.fill(
            x + BAR_BORDER,
            y + BAR_BORDER,
            x + BAR_WIDTH - BAR_BORDER,
            y + BAR_HEIGHT - BAR_BORDER,
            backgroundColor
        )
    }

    private fun renderFill(drawContext: DrawContext, x: Int, y: Int, normalizedValue: Float, color: Int) {
        val fillWidth = (BAR_INNER_WIDTH * normalizedValue.coerceIn(0f, 1f)).roundToInt()
        if (fillWidth <= 0) return

        val innerX = x + BAR_BORDER
        val innerY = y + BAR_BORDER
        drawContext.fill(innerX, innerY, innerX + fillWidth, innerY + BAR_INNER_HEIGHT, color)
    }

    private fun renderChip(drawContext: DrawContext, x: Int, y: Int, normalizedValue: Float, color: Int) {
        val fillWidth = (BAR_INNER_WIDTH * normalizedValue.coerceIn(0f, 1f)).roundToInt()
        if (fillWidth <= 0) return

        val innerX = x + BAR_BORDER
        val innerY = y + BAR_BORDER
        drawContext.fill(innerX, innerY, innerX + fillWidth, innerY + BAR_INNER_HEIGHT, color)
    }

    private fun renderAbsorption(
        drawContext: DrawContext,
        x: Int,
        y: Int,
        currentHealth: Float,
        maxHealth: Float,
        absorption: Float
    ) {
        val currentWidth = (BAR_INNER_WIDTH * (currentHealth / maxHealth).coerceIn(0f, 1f)).roundToInt()
        val absorptionWidth = (BAR_INNER_WIDTH * (absorption / maxHealth).coerceIn(0f, 1f)).roundToInt()
        if (absorptionWidth <= 0) return

        val startX = x + BAR_BORDER + currentWidth
        val endX = (startX + absorptionWidth).coerceAtMost(x + BAR_BORDER + BAR_INNER_WIDTH)
        val innerY = y + BAR_BORDER
        if (endX > startX) {
            drawContext.fill(startX, innerY, endX, innerY + BAR_INNER_HEIGHT, 0x99F0C445.toInt())
        }
    }

    private fun renderGloss(drawContext: DrawContext, x: Int, y: Int) {
        drawContext.fill(
            x + BAR_BORDER,
            y + BAR_BORDER,
            x + BAR_WIDTH - BAR_BORDER,
            y + BAR_BORDER + 2,
            0x33FFFFFF
        )
    }

    private fun renderCenteredText(drawContext: DrawContext, x: Int, y: Int, text: String, color: Int) {
        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer
        val textWidth = textRenderer.getWidth(text)
        val textX = x + (BAR_WIDTH - textWidth) / 2
        val textY = y + (BAR_HEIGHT - 8) / 2
        drawContext.drawText(textRenderer, text, textX, textY, color, true)
    }

    private fun shouldRenderBars(player: PlayerEntity?): Boolean {
        val client = MinecraftClient.getInstance()
        return player != null && !client.options.hudHidden
    }

    private fun smooth(current: Float, target: Float, factor: Float): Float {
        if (current < 0f) return target
        val next = MathHelper.lerp(factor, current, target)
        return if (abs(next - target) < 0.05f) target else next
    }

    private fun formatValue(value: Float): String {
        val rounded = value.roundToInt()
        return if (abs(value - rounded.toFloat()) < 0.05f) rounded.toString()
        else String.format(java.util.Locale.ROOT, "%.1f", value)
    }

    private fun lowHealthPulse(normalizedValue: Float): Float {
        if (normalizedValue > 0.3f) return 0f
        val wave = ((sin(System.currentTimeMillis() / 140.0) + 1.0) * 0.5).toFloat()
        return (wave * (1f - normalizedValue / 0.3f)).coerceIn(0f, 1f)
    }

    private fun interpolateColor(from: Int, to: Int, progress: Float): Int {
        val clamped = progress.coerceIn(0f, 1f)
        val a = lerpChannel(from ushr 24 and 0xFF, to ushr 24 and 0xFF, clamped)
        val r = lerpChannel(from ushr 16 and 0xFF, to ushr 16 and 0xFF, clamped)
        val g = lerpChannel(from ushr 8 and 0xFF, to ushr 8 and 0xFF, clamped)
        val b = lerpChannel(from and 0xFF, to and 0xFF, clamped)
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    private fun lerpChannel(from: Int, to: Int, progress: Float): Int {
        return (from + ((to - from) * progress)).roundToInt().coerceIn(0, 255)
    }

    private const val BAR_WIDTH = 82
    private const val BAR_HEIGHT = 12
    private const val BAR_BORDER = 1
    private const val BAR_INNER_WIDTH = BAR_WIDTH - (BAR_BORDER * 2)
    private const val BAR_INNER_HEIGHT = BAR_HEIGHT - (BAR_BORDER * 2)
    private const val DAMAGE_TRAIL_DELAY_MS = 180L
}
