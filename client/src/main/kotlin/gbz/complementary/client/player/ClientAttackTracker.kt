package gbz.complementary.client.player

import gbz.complementary.client.animation.CombatAnimationManager
import gbz.complementary.client.matcher.WeaponAnimationResolver
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback
import net.minecraft.client.network.AbstractClientPlayerEntity

class ClientAttackTracker(
    private val resolver: WeaponAnimationResolver,
    private val animationManager: CombatAnimationManager
) {
    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            val world = client.world ?: return@register
            for (player in world.players) {
                val resolution = resolver.resolve(player.mainHandStack)
                animationManager.ensureIdle(player, resolution.type, resolution.modelKey, resolution.matchedRule, resolution.priority)

                // Remote players have no attack callback; the vanilla swing packet
                // sets handSwinging and handSwingTicks passes through 0 exactly once
                // per swing, so this fires once at the start of each swing.
                if (player !== client.player && player.handSwinging && player.handSwingTicks == 0) {
                    animationManager.onAttack(player, player.mainHandStack, resolution.type)
                }
            }
        }

        ClientPreAttackCallback.EVENT.register { _, player, clickCount ->
            // Only play the attack animation when the vanilla attack cooldown has
            // mostly recovered; otherwise rapid clicking restarts the animation
            // every click and it never plays through.
            if (clickCount != 0 && player.getAttackCooldownProgress(0f) >= 0.9f) {
                val resolution = resolver.resolve(player.mainHandStack)
                animationManager.onAttack(player, player.mainHandStack, resolution.type)
            }
            false
        }

        ClientEntityEvents.ENTITY_UNLOAD.register { entity, _ ->
            if (entity is AbstractClientPlayerEntity) {
                animationManager.onPlayerUnload(entity)
            }
        }
    }
}
