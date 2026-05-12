package com.gbz.combat.plugin.animation

import com.gbz.combat.plugin.GbzCombatPlugin
import com.gbz.combat.plugin.network.ModDetectionManager
import com.gbz.combat.plugin.network.PluginPacketManager
import com.gbz.combat.plugin.weapon.WeaponClassifier
import com.gbz.combat.shared.AttackAnimationType
import com.gbz.combat.shared.packet.AttackAnimationPacket
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class AnimationSyncManager(
    private val plugin: GbzCombatPlugin,
    private val modDetectionManager: ModDetectionManager,
    private val packetManager: PluginPacketManager,
    private val weaponClassifier: WeaponClassifier
) {
    private val lastBroadcast = ConcurrentHashMap<UUID, Long>()

    fun handleAttack(player: Player, hand: InteractionHand) {
        val now = System.currentTimeMillis()
        val previous = lastBroadcast.put(player.uniqueId, now)
        if (previous != null && now - previous < 90L) {
            return
        }

        val slot = if (hand == InteractionHand.OFF_HAND) EquipmentSlot.OFF_HAND else EquipmentSlot.HAND
        val item = if (slot == EquipmentSlot.OFF_HAND) player.inventory.itemInOffHand else player.inventory.itemInMainHand
        val category = weaponClassifier.classify(item, slot)
        val packet = AttackAnimationPacket(
            entityId = player.entityId,
            animationType = AttackAnimationType.PRIMARY,
            timestamp = now,
            weaponCategory = category
        )
        val maxDistanceSquared = plugin.configService.current.packetDistance * plugin.configService.current.packetDistance

        player.world.players.asSequence()
            .filter { it.location.distanceSquared(player.location) <= maxDistanceSquared }
            .filter { modDetectionManager.isModded(it) }
            .forEach { packetManager.sendAttackAnimation(it, packet) }

        debug("Broadcast ${category.name} for ${player.name}")
    }

    private fun debug(message: String) {
        if (plugin.configService.current.debugEnabled) {
            plugin.logger.info("[debug] $message")
        }
    }
}
