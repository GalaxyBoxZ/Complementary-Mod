package com.gbz.combat.shared.packet

data class ClientHelloPacket(
    val protocolVersion: Int,
    val firstPersonEnabled: Boolean,
    val smoothTransitionsEnabled: Boolean
)
