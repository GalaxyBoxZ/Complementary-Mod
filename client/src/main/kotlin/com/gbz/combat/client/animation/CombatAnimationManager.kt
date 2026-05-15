package com.gbz.combat.client.animation

import com.gbz.combat.client.config.ConfigState
import com.gbz.combat.client.player.CombatPlayerState
import com.gbz.combat.client.registry.AnimationProfileRegistry
import com.gbz.combat.client.util.GbzCombatConstants.LOGGER
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode
import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer
import dev.kosmx.playerAnim.api.layered.ModifierLayer
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier
import dev.kosmx.playerAnim.core.data.KeyframeAnimation
import dev.kosmx.playerAnim.core.util.Ease
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

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
        if (state.currentType == resolvedType && state.modelKey == modelKey) {
            return
        }

        state.currentType = resolvedType
        state.modelKey = modelKey
        state.matchedRule = matchedRule
        state.matchPriority = priority
        state.comboStep = 0

        val layers = getLayers(player) ?: return
        val profile = AnimationProfileRegistry.get(resolvedType)
        val idleSpeed = profile.nominalIdleSpeed * configState.config.globalSpeedMultiplier
        layers.idleSpeed.speed = idleSpeed
        layers.idle.replaceAnimationWithFade(
            AbstractFadeModifier.standardFadeIn(configState.config.interpolationTicks.coerceAtLeast(1), Ease.LINEAR),
            createPlayer(profile.idleAnimationId, firstPersonMode = FirstPersonMode.DISABLED) ?: return
        )
    }

    fun onAttack(player: ClientPlayerEntity, stack: ItemStack, resolvedType: WeaponAnimationType) {
        val layers = getLayers(player) ?: return
        val profile = AnimationProfileRegistry.get(resolvedType)
        val attackSpeed = player.getAttributeValue(EntityAttributes.ATTACK_SPEED).toFloat().coerceAtLeast(0.1f)
        val vanillaCooldownTicks = 20.0f / attackSpeed
        val playbackSpeed = ((profile.nominalAttackTicks.toFloat() / vanillaCooldownTicks) * resolvedType.baseSpeedMultiplier * configState.config.globalSpeedMultiplier)
            .coerceIn(0.3f, 3.5f)

        state.currentType = resolvedType
        state.modelKey = state.modelKey.ifEmpty { stack.item.toString() }
        state.lastAttackSpeed = attackSpeed
        state.lastCooldownTicks = vanillaCooldownTicks
        state.lastPlaybackSpeed = playbackSpeed

        val comboIndex = state.comboStep % profile.attackAnimationIds.size
        state.comboStep = comboIndex + 1

        layers.attackSpeed.speed = playbackSpeed
        layers.attack.replaceAnimationWithFade(
            AbstractFadeModifier.standardFadeIn(profile.fadeInTicks.coerceAtLeast(1), Ease.LINEAR),
            createPlayer(profile.attackAnimationIds[comboIndex], profile.showLeftArmInFirstPerson, profile.showLeftItemInFirstPerson) ?: return
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

    private fun getLayers(player: AbstractClientPlayerEntity): PlayerLayers? =
        playerLayers[player.uuid] ?: if (player is ClientPlayerEntity) localPlayerLayers else null

    private fun createPlayer(
        animationId: Identifier,
        showLeftArm: Boolean = true,
        showLeftItem: Boolean = false,
        firstPersonMode: FirstPersonMode = FirstPersonMode.THIRD_PERSON_MODEL
    ): KeyframeAnimationPlayer? {
        val anim = PlayerAnimationRegistry.getAnimation(animationId) as? KeyframeAnimation ?: return null
        return KeyframeAnimationPlayer(anim)
            .setFirstPersonMode(firstPersonMode)
            .setFirstPersonConfiguration(
                FirstPersonConfiguration()
                    .setShowRightArm(true)
                    .setShowLeftArm(showLeftArm)
                    .setShowLeftItem(showLeftItem)
            )
    }

    companion object {
        private val playerLayers = ConcurrentHashMap<UUID, PlayerLayers>()
        @Volatile private var localPlayerLayers: PlayerLayers? = null

        fun registerPlayerLayers() {
            PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register { player, animationStack ->
                try {
                    val idleLayer = ModifierLayer<IAnimation>()
                    val attackLayer = ModifierLayer<IAnimation>()
                    val idleSpeed = SpeedModifier(1.0f)
                    val attackSpeed = SpeedModifier(1.0f)

                    idleLayer.addModifierBefore(idleSpeed)
                    attackLayer.addModifierBefore(attackSpeed)

                    animationStack.addAnimLayer(60, idleLayer)
                    animationStack.addAnimLayer(80, attackLayer)

                    val layers = PlayerLayers(idleLayer, attackLayer, idleSpeed, attackSpeed)
                    playerLayers[player.uuid] = layers
                    if (player is ClientPlayerEntity) localPlayerLayers = layers
                } catch (e: Exception) {
                    LOGGER.error("GBZ: failed to register layers for uuid={}", player.uuid, e)
                }
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
