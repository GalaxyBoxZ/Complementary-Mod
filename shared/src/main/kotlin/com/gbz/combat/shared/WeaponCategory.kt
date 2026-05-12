package com.gbz.combat.shared

enum class WeaponCategory {
    FIST,
    PICKAXE,
    AXE,
    DAGGER,
    SWORD,
    KATANA,
    LONGSWORD,
    BROADSWORD,
    SCYTHE,
    WARGLAIVE,
    GREATAXE;

    companion object {
        fun fromConfigValue(value: String): WeaponCategory? =
            entries.firstOrNull { it.name.equals(value.trim(), ignoreCase = true) }
    }
}
