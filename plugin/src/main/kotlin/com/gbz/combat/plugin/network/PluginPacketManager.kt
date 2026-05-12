package com.gbz.combat.plugin.network

import com.gbz.combat.plugin.GbzCombatPlugin
import com.gbz.combat.shared.NetworkChannels
import com.gbz.combat.shared.packet.AttackAnimationPacket
import com.gbz.combat.shared.packet.AttackAnimationPacketCodec
import org.bukkit.entity.Player

class PluginPacketManager(private val plugin: GbzCombatPlugin) {
    fun registerChannels() {
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, NetworkChannels.ATTACK_SYNC)
        plugin.server.messenger.registerIncomingPluginChannel(plugin, NetworkChannels.CLIENT_HELLO, plugin.modDetectionManager)
    }

    fun unregisterChannels() {
        plugin.server.messenger.unregisterOutgoingPluginChannel(plugin)
        plugin.server.messenger.unregisterIncomingPluginChannel(plugin)
    }

    fun sendAttackAnimation(player: Player, packet: AttackAnimationPacket) {
        player.sendPluginMessage(plugin, NetworkChannels.ATTACK_SYNC, AttackAnimationPacketCodec.encode(packet))
    }
}
