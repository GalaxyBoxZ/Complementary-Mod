package com.gbz.combat.plugin.listener

import com.gbz.combat.plugin.animation.AnimationSyncManager
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientAnimation
import org.bukkit.entity.Player

class AttackPacketListener(
    private val animationSyncManager: AnimationSyncManager
) : PacketListenerAbstract(PacketListenerPriority.NORMAL) {
    override fun onPacketReceive(event: PacketReceiveEvent) {
        if (event.packetType != PacketType.Play.Client.ANIMATION) {
            return
        }
        val player = event.player as? Player ?: return
        if (!player.isOnline || player.isDead) {
            return
        }
        val wrapper = WrapperPlayClientAnimation(event)
        animationSyncManager.handleAttack(player, wrapper.hand)
    }
}
