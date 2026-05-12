package com.gbz.combat.client.registry

import com.gbz.combat.client.animation.AnimationProfile
import com.gbz.combat.client.animation.WeaponAnimationType
import com.gbz.combat.client.util.GbzCombatConstants.id

object AnimationProfileRegistry {
    private val profiles: Map<WeaponAnimationType, AnimationProfile> = mapOf(
        WeaponAnimationType.FIST to AnimationProfile(WeaponAnimationType.FIST, id("fist_attack"), id("idle_light"), 8, 1.05f),
        WeaponAnimationType.PICKAXE to AnimationProfile(WeaponAnimationType.PICKAXE, id("pickaxe_attack"), id("idle_utility"), 12, 0.96f),
        WeaponAnimationType.AXE to AnimationProfile(WeaponAnimationType.AXE, id("axe_attack"), id("idle_utility"), 13, 0.92f),
        WeaponAnimationType.DAGGER to AnimationProfile(WeaponAnimationType.DAGGER, id("dagger_attack"), id("idle_light"), 8, 1.08f),
        WeaponAnimationType.SWORD to AnimationProfile(WeaponAnimationType.SWORD, id("sword_attack"), id("idle_light"), 11, 1.0f),
        WeaponAnimationType.KATANA to AnimationProfile(WeaponAnimationType.KATANA, id("katana_attack"), id("idle_light"), 10, 1.04f),
        WeaponAnimationType.LONGSWORD to AnimationProfile(WeaponAnimationType.LONGSWORD, id("longsword_attack"), id("idle_heavy"), 14, 0.9f),
        WeaponAnimationType.BROADSWORD to AnimationProfile(WeaponAnimationType.BROADSWORD, id("broadsword_attack"), id("idle_heavy"), 15, 0.88f),
        WeaponAnimationType.SCYTHE to AnimationProfile(WeaponAnimationType.SCYTHE, id("scythe_attack"), id("idle_polearm"), 15, 0.93f),
        WeaponAnimationType.WARGLAIVE to AnimationProfile(WeaponAnimationType.WARGLAIVE, id("warglaive_attack"), id("idle_polearm"), 12, 1.02f),
        WeaponAnimationType.GREATAXE to AnimationProfile(WeaponAnimationType.GREATAXE, id("greataxe_attack"), id("idle_heavy"), 17, 0.82f)
    )

    fun get(type: WeaponAnimationType): AnimationProfile = profiles.getValue(type)
}
