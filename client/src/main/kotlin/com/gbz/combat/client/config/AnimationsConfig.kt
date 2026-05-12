package com.gbz.combat.client.config

import com.gbz.combat.client.animation.WeaponAnimationType

data class AnimationMappingEntry(
    val match: String,
    val animation: String
)

data class AnimationsConfig(
    val default: String = WeaponAnimationType.FIST.id,
    val debugLogging: Boolean = false,
    val debugOverlay: Boolean = false,
    val interpolationTicks: Int = 3,
    val globalSpeedMultiplier: Float = 1.0f,
    val mappings: List<AnimationMappingEntry> = DEFAULT_MAPPINGS
) {
    companion object {
        val DEFAULT_MAPPINGS: List<AnimationMappingEntry> = listOf(
            AnimationMappingEntry("minecraft:*_pickaxe", "pickaxe"),
            AnimationMappingEntry("minecraft:*_axe", "axe"),
            AnimationMappingEntry("minecraft:*_sword", "sword"),
            AnimationMappingEntry("gbz:pickaxe/*", "pickaxe"),
            AnimationMappingEntry("gbz:weapon/cosmetic/admin", "warglaive"),
            AnimationMappingEntry("gbz:weapon/coal", "sword"),
            AnimationMappingEntry("gbz:weapon/enderstone", "katana")
        )
    }
}
