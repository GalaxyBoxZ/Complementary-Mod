package com.gbz.combat.client.player

import com.gbz.combat.client.animation.WeaponAnimationType

data class CombatPlayerState(
    var currentType: WeaponAnimationType = WeaponAnimationType.FIST,
    var modelKey: String = "minecraft:empty",
    var matchedRule: String? = null,
    var matchPriority: String = "default",
    var lastAttackSpeed: Float = 4.0f,
    var lastCooldownTicks: Float = 5.0f,
    var lastPlaybackSpeed: Float = 1.0f,
    var debugOverlayEnabled: Boolean = false
)
