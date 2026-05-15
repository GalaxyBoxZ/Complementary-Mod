package com.gbz.combat.client.animation

import net.minecraft.util.Identifier

data class AnimationProfile(
    val type: WeaponAnimationType,
    val attackAnimationIds: List<Identifier>,
    val idleAnimationId: Identifier,
    val nominalAttackTicks: Int,
    val nominalIdleSpeed: Float = 1.0f,
    val fadeInTicks: Int = 3,
    val showLeftArmInFirstPerson: Boolean = true,
    val showLeftItemInFirstPerson: Boolean = false
)
