package com.gbz.combat.client.animation

enum class WeaponAnimationType(
    val id: String,
    val baseSpeedMultiplier: Float
) {
    FIST("fist", 1.15f),
    PICKAXE("pickaxe", 0.92f),
    AXE("axe", 0.88f),
    DAGGER("dagger", 1.35f),
    SWORD("sword", 1.0f),
    KATANA("katana", 1.1f),
    LONGSWORD("longsword", 0.9f),
    BROADSWORD("broadsword", 0.82f),
    SCYTHE("scythe", 0.86f),
    WARGLAIVE("warglaive", 1.08f),
    GREATAXE("greataxe", 0.74f);

    companion object {
        private val BY_ID = entries.associateBy { it.id }

        fun fromId(id: String): WeaponAnimationType? = BY_ID[id.lowercase()]
    }
}
