package com.gbz.combat.shared.packet

import com.gbz.combat.shared.AttackAnimationType
import com.gbz.combat.shared.WeaponCategory

data class AttackAnimationPacket(
    val entityId: Int,
    val animationType: AttackAnimationType,
    val timestamp: Long,
    val weaponCategory: WeaponCategory
)
