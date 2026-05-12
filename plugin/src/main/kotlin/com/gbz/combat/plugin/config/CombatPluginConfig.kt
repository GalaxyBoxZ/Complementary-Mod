package com.gbz.combat.plugin.config

import com.gbz.combat.shared.WeaponCategory

data class CombatPluginConfig(
    val debugEnabled: Boolean,
    val animationSpeed: Double,
    val smoothTransitions: Boolean,
    val firstPersonEnabled: Boolean,
    val packetDistance: Double,
    val weaponMappings: Map<String, WeaponCategory>
)
