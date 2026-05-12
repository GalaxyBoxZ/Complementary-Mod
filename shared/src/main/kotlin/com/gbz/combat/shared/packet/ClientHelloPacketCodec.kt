package com.gbz.combat.shared.packet

object ClientHelloPacketCodec : BinaryPacketCodec<ClientHelloPacket> {
    override fun encode(packet: ClientHelloPacket): ByteArray = PacketBufferIO.write {
        writeInt(packet.protocolVersion)
        writeBoolean(packet.firstPersonEnabled)
        writeBoolean(packet.smoothTransitionsEnabled)
    }

    override fun decode(bytes: ByteArray): ClientHelloPacket = PacketBufferIO.read(bytes) {
        ClientHelloPacket(
            protocolVersion = readInt(),
            firstPersonEnabled = readBoolean(),
            smoothTransitionsEnabled = readBoolean()
        )
    }
}
