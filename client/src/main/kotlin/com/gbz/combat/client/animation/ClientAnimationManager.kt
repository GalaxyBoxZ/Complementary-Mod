package com.gbz.combat.client.animation

import com.gbz.combat.shared.packet.AttackAnimationPacket
import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.api.layered.ModifierLayer
import dev.kosmx.playerAnim.core.impl.AnimationProcessor
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.AbstractClientPlayerEntity
import java.util.concurrent.ConcurrentHashMap
import java.util.UUID

class ClientAnimationManager {
    private val states = ConcurrentHashMap<UUID, PlayerAnimationState>()

    fun play(packet: AttackAnimationPacket, client: MinecraftClient) {
        val world = client.world ?: return
        val entity = world.getEntityById(packet.entityId) as? AbstractClientPlayerEntity ?: return
        val state = states.computeIfAbsent(entity.uuid) {
            val layer = ModifierLayer<IAnimation>()
            PlayerAnimationAccess.getPlayerAnimLayer(entity).addAnimLayer(1000, layer)
            PlayerAnimationState(layer)
        }

        val animation = PlayerAnimationRegistry.getAnimation(AnimationKey.forCategory(packet.weaponCategory)) ?: return
        val player = AnimationProcessor(animation)
        val latencyMs = (System.currentTimeMillis() - packet.timestamp).coerceAtLeast(0L)
        val offsetTicks = (latencyMs / 50L).toInt()
        repeat(offsetTicks) {
            player.tick()
        }
        state.layer.setAnimation(player)
        state.lastStartedAt = System.currentTimeMillis()
    }

    fun clear() {
        states.values.forEach { it.layer.setAnimation(null) }
        states.clear()
    }
}
