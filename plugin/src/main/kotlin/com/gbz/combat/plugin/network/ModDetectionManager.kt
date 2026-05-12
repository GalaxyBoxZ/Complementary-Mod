package com.gbz.combat.plugin.network

import com.gbz.combat.plugin.GbzCombatPlugin
import com.gbz.combat.shared.NetworkChannels
import com.gbz.combat.shared.packet.ClientHelloPacketCodec
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.messaging.PluginMessageListener
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class ModdedClientSession(
    val protocolVersion: Int,
    val firstPersonEnabled: Boolean,
    val smoothTransitionsEnabled: Boolean
)

class ModDetectionManager(private val plugin: GbzCombatPlugin) : Listener, PluginMessageListener {
    private val sessions = ConcurrentHashMap<UUID, ModdedClientSession>()

    fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun unregister() {
        sessions.clear()
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
    }

    fun isModded(player: Player): Boolean = sessions.containsKey(player.uniqueId)

    fun getSession(player: Player): ModdedClientSession? = sessions[player.uniqueId]

    fun getModdedPlayerCount(): Int = sessions.size

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != NetworkChannels.CLIENT_HELLO) {
            return
        }
        val hello = ClientHelloPacketCodec.decode(message)
        if (hello.protocolVersion != NetworkChannels.PROTOCOL_VERSION) {
            plugin.logger.warning("Ignoring incompatible GBZ client on ${player.name}: protocol ${hello.protocolVersion}")
            return
        }
        sessions[player.uniqueId] = ModdedClientSession(
            protocolVersion = hello.protocolVersion,
            firstPersonEnabled = hello.firstPersonEnabled,
            smoothTransitionsEnabled = hello.smoothTransitionsEnabled
        )
        debug("Registered modded client ${player.name}")
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        sessions.remove(event.player.uniqueId)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        sessions.remove(event.player.uniqueId)
    }

    private fun debug(message: String) {
        if (plugin.configService.current.debugEnabled) {
            plugin.logger.info("[debug] $message")
        }
    }
}
