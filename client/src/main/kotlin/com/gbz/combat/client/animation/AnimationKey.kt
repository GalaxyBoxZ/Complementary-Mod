package com.gbz.combat.client.animation

import com.gbz.combat.shared.WeaponCategory
import net.minecraft.util.Identifier

object AnimationKey {
    fun forCategory(category: WeaponCategory): Identifier = Identifier.of("gbzcombat", category.name.lowercase())
}
