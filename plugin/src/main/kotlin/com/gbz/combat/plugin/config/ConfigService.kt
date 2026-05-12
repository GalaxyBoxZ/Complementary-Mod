package com.gbz.combat.plugin.config

import com.gbz.combat.plugin.GbzCombatPlugin
import com.gbz.combat.shared.WeaponCategory

class ConfigService(private val plugin: GbzCombatPlugin) {
    @Volatile
    var current: CombatPluginConfig = CombatPluginConfig(
        debugEnabled = false,
        animationSpeed = 1.0,
        smoothTransitions = true,
        firstPersonEnabled = true,
        packetDistance = 48.0,
        weaponMappings = emptyMap()
    )
        private set

    fun reload() {
        plugin.reloadConfig()
        val section = plugin.config.getConfigurationSection("weapon-categories")
        val mappings = buildMap {
            section?.getKeys(false)?.forEach { key ->
                val category = WeaponCategory.fromConfigValue(section.getString(key).orEmpty()) ?: return@forEach
                put(key.uppercase(), category)
            }
        }
        current = CombatPluginConfig(
            debugEnabled = plugin.config.getBoolean("enable-debug", false),
            animationSpeed = plugin.config.getDouble("animation-speed", 1.0),
            smoothTransitions = plugin.config.getBoolean("smooth-transitions", true),
            firstPersonEnabled = plugin.config.getBoolean("enable-first-person", true),
            packetDistance = plugin.config.getDouble("packet-distance", 48.0),
            weaponMappings = mappings
        )
    }
}
