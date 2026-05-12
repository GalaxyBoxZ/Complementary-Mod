package com.gbz.combat.plugin.weapon

import com.gbz.combat.plugin.GbzCombatPlugin
import com.gbz.combat.shared.WeaponCategory
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class WeaponClassifier(private val plugin: GbzCombatPlugin) {
    private val customWeaponKey = NamespacedKey(plugin, "weapon_id")

    fun classify(item: ItemStack?, slot: EquipmentSlot): WeaponCategory {
        if (item == null || item.type == Material.AIR) {
            return WeaponCategory.FIST
        }

        val meta = item.itemMeta
        val pdcId = meta?.persistentDataContainer?.get(customWeaponKey, PersistentDataType.STRING)
        if (!pdcId.isNullOrBlank()) {
            resolveConfiguredCategory(pdcId)?.let { return it }
        }

        resolveConfiguredCategory(item.type.name)?.let { return it }
        resolveConfiguredCategory(item.type.key.toString())?.let { return it }

        return inferVanillaCategory(item.type, slot)
    }

    private fun resolveConfiguredCategory(id: String): WeaponCategory? {
        return plugin.configService.current.weaponMappings[id.uppercase()]
    }

    private fun inferVanillaCategory(material: Material, slot: EquipmentSlot): WeaponCategory {
        val key = material.key.key
        return when {
            key.contains("dagger") -> WeaponCategory.DAGGER
            key.contains("katana") -> WeaponCategory.KATANA
            key.contains("scythe") -> WeaponCategory.SCYTHE
            key.contains("glaive") -> WeaponCategory.WARGLAIVE
            key.contains("broadsword") -> WeaponCategory.BROADSWORD
            key.contains("longsword") -> WeaponCategory.LONGSWORD
            key.contains("greataxe") -> WeaponCategory.GREATAXE
            key.contains("pickaxe") -> WeaponCategory.PICKAXE
            key.contains("axe") -> WeaponCategory.AXE
            key.contains("sword") -> if (slot == EquipmentSlot.HAND) WeaponCategory.SWORD else WeaponCategory.DAGGER
            else -> WeaponCategory.FIST
        }
    }
}
