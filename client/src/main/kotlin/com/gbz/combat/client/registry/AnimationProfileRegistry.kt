package com.gbz.combat.client.registry

import com.gbz.combat.client.animation.AnimationProfile
import com.gbz.combat.client.animation.WeaponAnimationType
import com.gbz.combat.client.util.GbzCombatConstants.id

object AnimationProfileRegistry {
    private val profiles: Map<WeaponAnimationType, AnimationProfile> = mapOf(
        WeaponAnimationType.FIST       to AnimationProfile(WeaponAnimationType.FIST,       id("attack_fist"),       id("idle_fist"),       7,  1.05f),
        WeaponAnimationType.PICKAXE    to AnimationProfile(WeaponAnimationType.PICKAXE,    id("attack_pickaxe"),    id("idle_pickaxe"),    11, 0.96f),
        WeaponAnimationType.AXE        to AnimationProfile(WeaponAnimationType.AXE,        id("attack_axe"),        id("idle_axe"),        12, 0.92f),
        WeaponAnimationType.DAGGER     to AnimationProfile(WeaponAnimationType.DAGGER,     id("attack_dagger"),     id("idle_dagger"),     7,  1.08f),
        WeaponAnimationType.SWORD      to AnimationProfile(WeaponAnimationType.SWORD,      id("attack_sword"),      id("idle_sword"),      11, 1.0f),
        WeaponAnimationType.KATANA     to AnimationProfile(WeaponAnimationType.KATANA,     id("attack_katana"),     id("idle_katana"),     10, 1.04f),
        WeaponAnimationType.LONGSWORD  to AnimationProfile(WeaponAnimationType.LONGSWORD,  id("attack_longsword"),  id("idle_longsword"),  15, 0.9f),
        WeaponAnimationType.BROADSWORD to AnimationProfile(WeaponAnimationType.BROADSWORD, id("attack_broadsword"), id("idle_broadsword"), 16, 0.88f),
        WeaponAnimationType.SCYTHE     to AnimationProfile(WeaponAnimationType.SCYTHE,     id("attack_scythe"),     id("idle_scythe"),     14, 0.93f),
        WeaponAnimationType.WARGLAIVE  to AnimationProfile(WeaponAnimationType.WARGLAIVE,  id("attack_warglaive"),  id("idle_warglaive"),  12, 1.02f),
        WeaponAnimationType.GREATAXE   to AnimationProfile(WeaponAnimationType.GREATAXE,   id("attack_greataxe"),   id("idle_greataxe"),   18, 0.82f)
    )

    fun get(type: WeaponAnimationType): AnimationProfile = profiles.getValue(type)
}
