package com.gbz.combat.client.config

import com.gbz.combat.client.matcher.WeaponAnimationMatcher
import com.gbz.combat.client.util.GbzCombatConstants.LOGGER
import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.io.Reader
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter

class AnimationConfigRepository {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val configDir: Path = FabricLoader.getInstance().configDir.resolve("gbzcombat")
    val configPath: Path = configDir.resolve("animations.json")

    @Volatile
    private var state: ConfigState = loadInternal(createIfMissing = true)

    fun current(): ConfigState = state

    fun reload(): ConfigState {
        val reloaded = loadInternal(createIfMissing = true)
        state = reloaded
        return reloaded
    }

    private fun loadInternal(createIfMissing: Boolean): ConfigState {
        Files.createDirectories(configDir)

        if (createIfMissing && Files.notExists(configPath)) {
            configPath.bufferedWriter().use { writer: Writer ->
                gson.toJson(AnimationsConfig(), writer)
            }
        }

        val config = configPath.bufferedReader().use { reader: Reader ->
            gson.fromJson(reader, AnimationsConfig::class.java) ?: AnimationsConfig()
        }
        val matcher = WeaponAnimationMatcher.compile(config)
        LOGGER.info("Loaded GBZ combat animation config from {}", configPath)
        return ConfigState(config, matcher)
    }
}

data class ConfigState(
    val config: AnimationsConfig,
    val matcher: WeaponAnimationMatcher
)
