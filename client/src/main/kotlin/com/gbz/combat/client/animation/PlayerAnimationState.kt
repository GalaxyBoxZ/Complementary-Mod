package com.gbz.combat.client.animation

import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.api.layered.ModifierLayer

data class PlayerAnimationState(
    val layer: ModifierLayer<IAnimation>,
    var lastStartedAt: Long = 0L
)
