package com.gbz.combat.client.player

import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries

object ItemModelResolver {
    fun resolveModelKey(stack: ItemStack): String {
        if (stack.isEmpty) {
            return "minecraft:empty"
        }

        val modelId = stack.get(DataComponentTypes.ITEM_MODEL)
        if (modelId != null) {
            return modelId.toString()
        }

        return Registries.ITEM.getId(stack.item).toString()
    }
}
