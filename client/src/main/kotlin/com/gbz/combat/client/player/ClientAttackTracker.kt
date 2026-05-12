package com.gbz.combat.client.player

import com.gbz.combat.client.animation.CombatAnimationManager
import com.gbz.combat.client.matcher.WeaponAnimationResolver
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback

class ClientAttackTracker(
    private val resolver: WeaponAnimationResolver,
    private val animationManager: CombatAnimationManager
) {
    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            client.player?.let { player ->
                val resolution = resolver.resolve(player.mainHandStack)
                animationManager.ensureIdle(player, resolution.type, resolution.modelKey, resolution.matchedRule, resolution.priority)
            }
        }

        ClientPreAttackCallback.EVENT.register { _, player, clickCount ->
            if (clickCount != 0) {
                val resolution = resolver.resolve(player.mainHandStack)
                animationManager.onAttack(player, player.mainHandStack, resolution.type)
            }
            false
        }
    }
}
