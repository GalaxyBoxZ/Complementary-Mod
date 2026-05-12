package com.gbz.combat.client.network

import com.gbz.combat.shared.NetworkChannels
import com.gbz.combat.shared.packet.ClientHelloPacket
import com.gbz.combat.shared.packet.ClientHelloPacketCodec
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

data class ClientHelloPayload(val packet: ClientHelloPacket) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    companion object {
        val ID = CustomPayload.Id<ClientHelloPayload>(Identifier.of(NetworkChannels.NAMESPACE, "client_hello"))
        val CODEC: PacketCodec<RegistryByteBuf, ClientHelloPayload> =
            PacketCodec.ofStatic(::write, ::read)

        private fun write(buf: RegistryByteBuf, payload: ClientHelloPayload) {
            buf.writeByteArray(ClientHelloPacketCodec.encode(payload.packet))
        }

        private fun read(buf: RegistryByteBuf): ClientHelloPayload {
            return ClientHelloPayload(ClientHelloPacketCodec.decode(buf.readByteArray()))
        }
    }
}
