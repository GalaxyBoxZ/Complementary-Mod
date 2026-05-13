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
        cache[modelKey]?.let { return it }
        val result = matcher.resolve(modelKey)
        val resolution = Resolution(modelKey, result.type, result.matchedRule, result.priority)
        if (result.priority != "default") cache[modelKey] = resolution
        return resolution
    }

    fun cacheSize(): Int = cache.size

    fun reload(matcher: WeaponAnimationMatcher) {
        this.matcher = matcher
        cache.clear()
    }
}
