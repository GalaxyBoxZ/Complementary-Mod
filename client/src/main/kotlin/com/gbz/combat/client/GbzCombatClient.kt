package com.gbz.combat.client

import com.gbz.combat.client.animation.CombatAnimationManager
import com.gbz.combat.client.config.AnimationConfigRepository
import com.gbz.combat.client.config.ConfigHotReloadService
import com.gbz.combat.client.matcher.WeaponAnimationResolver
import com.gbz.combat.client.player.ClientAttackTracker
import com.gbz.combat.client.registry.CombatCommands
import com.gbz.combat.client.util.DebugOverlayRenderer
import com.gbz.combat.client.util.GbzCombatConstants.LOGGER
import net.fabricmc.api.ClientModInitializer

object GbzCombatClient : ClientModInitializer {
    override fun onInitializeClient() {
        val repository = AnimationConfigRepository()
        val initialState = repository.current()
        val resolver = WeaponAnimationResolver(initialState.matcher)
        val animationManager = CombatAnimationManager(initialState)

        CombatAnimationManager.registerPlayerLayers()

        ClientAttackTracker(resolver, animationManager).register()
        DebugOverlayRenderer(animationManager, resolver).register()
        CombatCommands(repository, resolver, animationManager).register()

        animationManager.setDebugOverlayEnabled(initialState.config.debugOverlay)

        ConfigHotReloadService(repository) { state ->
            resolver.reload(state.matcher)
            animationManager.onConfigReload(state)
            animationManager.setDebugOverlayEnabled(state.config.debugOverlay)
        }.start()

        LOGGER.info("Initialized GBZ client combat animation system")
    }
}
