package gbz.complementary.client.config

import gbz.complementary.client.matcher.WeaponAnimationMatcher
import com.google.gson.GsonBuilder

class AnimationConfigRepository {
    private val gson = GsonBuilder().create()
    private val state: ConfigState = load()

    fun current(): ConfigState = state
    fun reload(): ConfigState = state

    private fun load(): ConfigState {
        val stream = AnimationConfigRepository::class.java.classLoader
            .getResourceAsStream("assets/gbz/animations.json")
            ?: error("animations.json not found in mod resources")

        val config = stream.bufferedReader().use { reader ->
            gson.fromJson(reader, AnimationsConfig::class.java) ?: AnimationsConfig()
        }
        return ConfigState(config, WeaponAnimationMatcher.compile(config))
    }
}

data class ConfigState(
    val config: AnimationsConfig,
    val matcher: WeaponAnimationMatcher
)
