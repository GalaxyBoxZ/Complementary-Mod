package com.gbz.combat.client

import com.gbz.combat.client.animation.ClientAnimationManager
import com.gbz.combat.client.network.AttackAnimationPayload
import com.gbz.combat.client.network.ClientHelloPayload
import com.gbz.combat.shared.NetworkChannels
import com.gbz.combat.shared.packet.ClientHelloPacket
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry

object GbzCombatClient : ClientModInitializer {
    lateinit var animationManager: ClientAnimationManager
        private set

    override fun onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(AttackAnimationPayload.ID, AttackAnimationPayload.CODEC)
        PayloadTypeRegistry.playC2S().register(ClientHelloPayload.ID, ClientHelloPayload.CODEC)

        animationManager = ClientAnimationManager()

        ClientPlayNetworking.registerGlobalReceiver(AttackAnimationPayload.ID) { payload, context ->
            animationManager.play(payload.packet, context.client())
        }

        ClientPlayConnectionEvents.JOIN.register { _, _, client ->
            ClientPlayNetworking.send(
                ClientHelloPayload(
                    ClientHelloPacket(
                        protocolVersion = NetworkChannels.PROTOCOL_VERSION,
                        firstPersonEnabled = true,
                        smoothTransitionsEnabled = true
                    )
                )
            )
            animationManager.clear()
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, client ->
            animationManager.clear()
        }
    }
}
