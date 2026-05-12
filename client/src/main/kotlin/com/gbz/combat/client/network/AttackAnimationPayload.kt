package com.gbz.combat.client.network

import com.gbz.combat.shared.NetworkChannels
import com.gbz.combat.shared.packet.AttackAnimationPacket
import com.gbz.combat.shared.packet.AttackAnimationPacketCodec
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

data class AttackAnimationPayload(val packet: AttackAnimationPacket) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    companion object {
        val ID = CustomPayload.Id<AttackAnimationPayload>(Identifier.of(NetworkChannels.NAMESPACE, "attack_sync"))
        val CODEC: PacketCodec<RegistryByteBuf, AttackAnimationPayload> =
            PacketCodec.ofStatic(::write, ::read)

        private fun write(buf: RegistryByteBuf, payload: AttackAnimationPayload) {
            val bytes = AttackAnimationPacketCodec.encode(payload.packet)
            buf.writeByteArray(bytes)
        }

        private fun read(buf: RegistryByteBuf): AttackAnimationPayload {
            return AttackAnimationPayload(AttackAnimationPacketCodec.decode(buf.readByteArray()))
        }
    }
}
