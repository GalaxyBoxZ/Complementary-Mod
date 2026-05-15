package com.gbz.combat.client.registry

import com.gbz.combat.client.animation.AnimationProfile
import com.gbz.combat.client.animation.WeaponAnimationType
import com.gbz.combat.client.util.GbzCombatConstants.id

object AnimationProfileRegistry {
    private val profiles: Map<WeaponAnimationType, AnimationProfile> = mapOf(
        WeaponAnimationType.FIST to AnimationProfile(
            WeaponAnimationType.FIST,
            listOf(id("attack_fist")),
            id("idle_fist"), 7
        ),
        WeaponAnimationType.PICKAXE to AnimationProfile(
            WeaponAnimationType.PICKAXE,
            listOf(id("attack_pickaxe"), id("attack_pickaxe_2")),
            id("idle_pickaxe"), 11, 0.96f
        ),
        WeaponAnimationType.AXE to AnimationProfile(
            WeaponAnimationType.AXE,
            listOf(id("attack_axe"), id("attack_axe_2")),
            id("idle_axe"), 12, 0.92f
        ),
        WeaponAnimationType.DAGGER to AnimationProfile(
            WeaponAnimationType.DAGGER,
            listOf(id("attack_dagger"), id("attack_dagger_2"), id("attack_dagger_3")),
            id("idle_dagger"), 7, 1.08f
        ),
        WeaponAnimationType.SWORD to AnimationProfile(
            WeaponAnimationType.SWORD,
            listOf(id("attack_sword"), id("attack_sword_2"), id("attack_sword_3")),
            id("idle_sword"), 11
        ),
        WeaponAnimationType.KATANA to AnimationProfile(
            WeaponAnimationType.KATANA,
            listOf(id("attack_katana"), id("attack_katana_2"), id("attack_katana_3")),
            id("idle_katana"), 10, 1.04f
        ),
        WeaponAnimationType.LONGSWORD to AnimationProfile(
            WeaponAnimationType.LONGSWORD,
            listOf(id("attack_longsword"), id("attack_longsword_2"), id("attack_longsword_3")),
            id("idle_longsword"), 15, 0.9f
        ),
        WeaponAnimationType.BROADSWORD to AnimationProfile(
            WeaponAnimationType.BROADSWORD,
            listOf(id("attack_broadsword"), id("attack_broadsword_2"), id("attack_broadsword_3")),
            id("idle_broadsword"), 16, 0.88f
        ),
        WeaponAnimationType.SCYTHE to AnimationProfile(
            WeaponAnimationType.SCYTHE,
            listOf(id("attack_scythe"), id("attack_scythe_2")),
            id("idle_scythe"), 14, 0.93f
        ),
        WeaponAnimationType.WARGLAIVE to AnimationProfile(
            WeaponAnimationType.WARGLAIVE,
            listOf(id("attack_warglaive"), id("attack_warglaive_2"), id("attack_warglaive_3")),
            id("idle_warglaive"), 12, 1.02f
        ),
        WeaponAnimationType.GREATAXE to AnimationProfile(
            WeaponAnimationType.GREATAXE,
            listOf(id("attack_greataxe"), id("attack_greataxe_2")),
            id("idle_greataxe"), 18, 0.82f
        )
    )

    fun get(type: WeaponAnimationType): AnimationProfile = profiles.getValue(type)
}
