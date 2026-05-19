package gbz.complementary.client.config

import gbz.complementary.client.animation.WeaponAnimationType

data class AnimationMappingEntry(
    val match: List<String>,
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
        val DEFAULT_MAPPINGS: List<AnimationMappingEntry> = emptyList()
    }
}
