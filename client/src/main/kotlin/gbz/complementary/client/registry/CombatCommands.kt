package gbz.complementary.client.registry

import gbz.complementary.client.animation.CombatAnimationManager
import gbz.complementary.client.config.AnimationConfigRepository
import gbz.complementary.client.matcher.WeaponAnimationResolver
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.text.Text

class CombatCommands(
    private val repository: AnimationConfigRepository,
    private val resolver: WeaponAnimationResolver,
    private val animationManager: CombatAnimationManager
) {
    fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                literal("combatanim")
                    .then(
                        literal("reload").executes { context ->
                            return@executes try {
                                val state = repository.reload()
                                resolver.reload(state.matcher)
                                animationManager.onConfigReload(state)
                                animationManager.setDebugOverlayEnabled(state.config.debugOverlay)
                                context.source.sendFeedback(Text.translatable("gbz.command.reload"))
                                1
                            } catch (exception: Exception) {
                                context.source.sendError(Text.translatable("gbz.command.reload.failed"))
                                0
                            }
                        }
                    )
                    .then(
                        literal("toggle").executes { context ->
                            val enabled = !animationManager.animationsEnabled
                            animationManager.setAnimationsEnabled(enabled)
                            context.source.sendFeedback(
                                if (enabled) {
                                    Text.translatable("gbz.command.animations.on")
                                } else {
                                    Text.translatable("gbz.command.animations.off")
                                }
                            )
                            1
                        }
                    )
                    .then(
                        literal("debug").executes { context ->
                            val enabled = !animationManager.currentState().debugOverlayEnabled
                            animationManager.setDebugOverlayEnabled(enabled)
                            context.source.sendFeedback(
                                if (enabled) {
                                    Text.translatable("gbz.command.debug.on")
                                } else {
                                    Text.translatable("gbz.command.debug.off")
                                }
                            )
                            1
                        }
                    )
            )
        }
    }
}
