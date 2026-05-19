package gbz.complementary.client

import gbz.complementary.client.animation.CombatAnimationManager
import gbz.complementary.client.config.AnimationConfigRepository
import gbz.complementary.client.hud.ItemCooldownHudRenderer
import gbz.complementary.client.matcher.WeaponAnimationResolver
import gbz.complementary.client.player.ClientAttackTracker
import gbz.complementary.client.registry.CombatCommands
import gbz.complementary.client.util.DebugOverlayRenderer
import gbz.complementary.client.util.GbzCombatConstants.LOGGER
import net.fabricmc.api.ClientModInitializer

object GBZComplementaryClient : ClientModInitializer {
    override fun onInitializeClient() {
        val repository = AnimationConfigRepository()
        val initialState = repository.current()
        val resolver = WeaponAnimationResolver(initialState.matcher)
        val animationManager = CombatAnimationManager(initialState)

        CombatAnimationManager.registerPlayerLayers()

        ClientAttackTracker(resolver, animationManager).register()
        ItemCooldownHudRenderer.register()
        DebugOverlayRenderer(animationManager, resolver).register()
        CombatCommands(repository, resolver, animationManager).register()

        animationManager.setDebugOverlayEnabled(initialState.config.debugOverlay)

        LOGGER.info("Initialized GBZ client")
    }
}
