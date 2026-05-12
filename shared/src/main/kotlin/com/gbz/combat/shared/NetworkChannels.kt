package com.gbz.combat.shared

object NetworkChannels {
    const val NAMESPACE = "gbzcombat"
    const val ATTACK_SYNC = "$NAMESPACE:attack_sync"
    const val CLIENT_HELLO = "$NAMESPACE:client_hello"
    const val PROTOCOL_VERSION = 1
}
