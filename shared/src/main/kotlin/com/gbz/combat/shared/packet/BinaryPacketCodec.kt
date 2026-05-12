package com.gbz.combat.shared.packet

interface BinaryPacketCodec<T> {
    fun encode(packet: T): ByteArray
    fun decode(bytes: ByteArray): T
}
