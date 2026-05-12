package com.gbz.combat.plugin

import com.gbz.combat.plugin.animation.AnimationSyncManager
import com.gbz.combat.plugin.command.CombatCommand
import com.gbz.combat.plugin.config.ConfigService
import com.gbz.combat.plugin.network.ModDetectionManager
import com.gbz.combat.plugin.network.PluginPacketManager
import com.gbz.combat.plugin.weapon.WeaponClassifier
import com.gbz.combat.plugin.listener.AttackPacketListener
import com.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.plugin.java.JavaPlugin

class GbzCombatPlugin : JavaPlugin() {
    lateinit var configService: ConfigService
        private set
    lateinit var modDetectionManager: ModDetectionManager
        private set
    lateinit var packetManager: PluginPacketManager
        private set
    lateinit var weaponClassifier: WeaponClassifier
        private set
    lateinit var animationSyncManager: AnimationSyncManager
        private set

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()
    }

    override fun onEnable() {
        saveDefaultConfig()
        val combatCommand = CombatCommand(this)

        configService = ConfigService(this).also { it.reload() }
        packetManager = PluginPacketManager(this)
        modDetectionManager = ModDetectionManager(this)
        weaponClassifier = WeaponClassifier(this)
        animationSyncManager = AnimationSyncManager(this, modDetectionManager, packetManager, weaponClassifier)

        packetManager.registerChannels()
        modDetectionManager.register()

        PacketEvents.getAPI().init()
        PacketEvents.getAPI().eventManager.registerListener(AttackPacketListener(animationSyncManager))

        getCommand("combat")?.setExecutor(combatCommand)
        getCommand("combat")?.tabCompleter = combatCommand
    }

    override fun onDisable() {
        modDetectionManager.unregister()
        packetManager.unregisterChannels()
        PacketEvents.getAPI().terminate()
    }
}
