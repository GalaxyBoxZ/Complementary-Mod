package com.gbz.combat.client.animation

import com.gbz.combat.client.config.ConfigState
import com.gbz.combat.client.player.CombatPlayerState
import com.gbz.combat.client.registry.AnimationProfileRegistry
import com.gbz.combat.client.util.GbzCombatConstants.LOGGER
import com.gbz.combat.client.util.GbzCombatConstants.id
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode
import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer
import dev.kosmx.playerAnim.api.layered.ModifierLayer
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier
import dev.kosmx.playerAnim.core.util.Ease
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

class CombatAnimationManager(
    private var configState: ConfigState
) {
    private val state = CombatPlayerState()

    fun currentState(): CombatPlayerState = state

    fun setDebugOverlayEnabled(enabled: Boolean) {
        state.debugOverlayEnabled = enabled
    }

    fun onConfigReload(configState: ConfigState) {
        this.configState = configState
    }

    fun ensureIdle(player: ClientPlayerEntity, resolvedType: WeaponAnimationType, modelKey: String, matchedRule: String?, priority: String) {
        val layers = getLayers(player) ?: return
        if (state.currentType == resolvedType && state.modelKey == modelKey) {
            return
        }

        state.currentType = resolvedType
        state.modelKey = modelKey
        state.matchedRule = matchedRule
        state.matchPriority = priority

        val profile = AnimationProfileRegistry.get(resolvedType)
        val idleSpeed = profile.nominalIdleSpeed * configState.config.globalSpeedMultiplier
        layers.idleSpeed.speed = idleSpeed
        layers.idle.replaceAnimationWithFade(
            AbstractFadeModifier.standardFadeIn(configState.config.interpolationTicks.coerceAtLeast(1), Ease.LINEAR),
            createPlayer(profile.idleAnimationId)
        )
    }

    fun onAttack(player: ClientPlayerEntity, stack: ItemStack, resolvedType: WeaponAnimationType) {
        val layers = getLayers(player) ?: return
        val profile = AnimationProfileRegistry.get(resolvedType)
        val attackSpeed = player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED).toFloat().coerceAtLeast(0.1f)
        val vanillaCooldownTicks = 20.0f / attackSpeed
        val playbackSpeed = ((profile.nominalAttackTicks.toFloat() / vanillaCooldownTicks) * resolvedType.baseSpeedMultiplier * configState.config.globalSpeedMultiplier)
            .coerceIn(0.3f, 3.5f)

        state.currentType = resolvedType
        state.modelKey = state.modelKey.ifEmpty { stack.item.toString() }
        state.lastAttackSpeed = attackSpeed
        state.lastCooldownTicks = vanillaCooldownTicks
        state.lastPlaybackSpeed = playbackSpeed

        layers.attackSpeed.speed = playbackSpeed
        layers.attack.replaceAnimationWithFade(
            AbstractFadeModifier.standardFadeIn(profile.fadeInTicks.coerceAtLeast(1), Ease.LINEAR),
            createPlayer(profile.attackAnimationId, profile.showLeftArmInFirstPerson, profile.showLeftItemInFirstPerson)
        )

        if (configState.config.debugLogging) {
            LOGGER.info(
                "Attack animation {} for {} model={} speed={} cooldownTicks={}",
                resolvedType.id,
                player.name.string,
                state.modelKey,
                playbackSpeed,
                vanillaCooldownTicks
            )
        }
    }

    private fun getLayers(player: AbstractClientPlayerEntity): PlayerLayers? {
        return PlayerAnimationAccess.getPlayerAssociatedData(player).get(id("layers")) as? PlayerLayers
    }

    private fun createPlayer(
        animationId: Identifier,
        showLeftArm: Boolean = true,
        showLeftItem: Boolean = false
    ): KeyframeAnimationPlayer {
        return KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(animationId))
            .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
            .setFirstPersonConfiguration(
                FirstPersonConfiguration()
                    .setShowRightArm(true)
                    .setShowLeftArm(showLeftArm)
                    .setShowLeftItem(showLeftItem)
            )
    }

    companion object {
        fun registerPlayerLayers() {
            PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register { player, animationStack ->
                val idleLayer = ModifierLayer<IAnimation>()
                val attackLayer = ModifierLayer<IAnimation>()
                val idleSpeed = SpeedModifier(1.0f)
                val attackSpeed = SpeedModifier(1.0f)

                idleLayer.addModifierBefore(idleSpeed)
                attackLayer.addModifierBefore(attackSpeed)

                animationStack.addAnimLayer(60, idleLayer)
                animationStack.addAnimLayer(80, attackLayer)
                PlayerAnimationAccess.getPlayerAssociatedData(player).set(id("layers"), PlayerLayers(idleLayer, attackLayer, idleSpeed, attackSpeed))
            }
        }
    }
}

data class PlayerLayers(
    val idle: ModifierLayer<IAnimation>,
    val attack: ModifierLayer<IAnimation>,
    val idleSpeed: SpeedModifier,
    val attackSpeed: SpeedModifier
)
