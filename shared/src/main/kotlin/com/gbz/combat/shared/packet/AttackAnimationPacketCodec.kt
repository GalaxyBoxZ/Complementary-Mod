package com.gbz.combat.shared.packet

import com.gbz.combat.shared.AttackAnimationType
import com.gbz.combat.shared.WeaponCategory

object AttackAnimationPacketCodec : BinaryPacketCodec<AttackAnimationPacket> {
    override fun encode(packet: AttackAnimationPacket): ByteArray = PacketBufferIO.write {
        writeInt(packet.entityId)
        writeUTF(packet.animationType.name)
        writeLong(packet.timestamp)
        writeUTF(packet.weaponCategory.name)
    }

    override fun decode(bytes: ByteArray): AttackAnimationPacket = PacketBufferIO.read(bytes) {
        AttackAnimationPacket(
            entityId = readInt(),
            animationType = AttackAnimationType.valueOf(readUTF()),
            timestamp = readLong(),
            weaponCategory = WeaponCategory.valueOf(readUTF())
        )
    }
}
