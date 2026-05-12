package com.gbz.combat.client.matcher

import com.gbz.combat.client.animation.WeaponAnimationType
import com.gbz.combat.client.player.ItemModelResolver
import net.minecraft.item.ItemStack
import java.util.concurrent.ConcurrentHashMap

class WeaponAnimationResolver(
    private var matcher: WeaponAnimationMatcher
) {
    data class Resolution(
        val modelKey: String,
        val type: WeaponAnimationType,
        val matchedRule: String?,
        val priority: String
    )

    private val cache = ConcurrentHashMap<String, Resolution>()

    fun resolve(stack: ItemStack): Resolution {
        val modelKey = ItemModelResolver.resolveModelKey(stack)
        return cache.computeIfAbsent(modelKey) {
            val result = matcher.resolve(modelKey)
            Resolution(modelKey, result.type, result.matchedRule, result.priority)
        }
    }

    fun cacheSize(): Int = cache.size

    fun reload(matcher: WeaponAnimationMatcher) {
        this.matcher = matcher
        cache.clear()
    }
}
