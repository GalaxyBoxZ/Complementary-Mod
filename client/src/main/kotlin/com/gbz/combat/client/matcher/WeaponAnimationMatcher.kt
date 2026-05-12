package com.gbz.combat.client.matcher

import com.gbz.combat.client.animation.WeaponAnimationType
import com.gbz.combat.client.config.AnimationsConfig

class WeaponAnimationMatcher private constructor(
    private val defaultType: WeaponAnimationType,
    private val exact: Map<String, WeaponAnimationType>,
    private val prefixes: List<CompiledPrefixRule>,
    private val wildcards: List<CompiledWildcardRule>
) {
    data class MatchResult(
        val type: WeaponAnimationType,
        val matchedRule: String?,
        val priority: String
    )

    fun resolve(modelKey: String): MatchResult {
        exact[modelKey]?.let { return MatchResult(it, modelKey, "exact") }

        for (rule in prefixes) {
            if (modelKey.startsWith(rule.prefix)) {
                return MatchResult(rule.type, rule.rawPattern, "prefix")
            }
        }

        for (rule in wildcards) {
            if (rule.regex.matches(modelKey)) {
                return MatchResult(rule.type, rule.rawPattern, "wildcard")
            }
        }

        return MatchResult(defaultType, null, "default")
    }

    companion object {
        fun compile(config: AnimationsConfig): WeaponAnimationMatcher {
            val defaultType = WeaponAnimationType.fromId(config.default) ?: WeaponAnimationType.FIST
            val exact = LinkedHashMap<String, WeaponAnimationType>()
            val prefixes = ArrayList<CompiledPrefixRule>()
            val wildcards = ArrayList<CompiledWildcardRule>()

            for (entry in config.mappings) {
                val type = WeaponAnimationType.fromId(entry.animation) ?: continue
                for (pattern in entry.match.map { it.trim() }.filter { it.isNotEmpty() }) {
                    when {
                        pattern.indexOf('*') == -1 -> exact[pattern] = type
                        pattern.endsWith("*") && pattern.count { it == '*' } == 1 -> {
                            prefixes += CompiledPrefixRule(pattern, pattern.dropLast(1), type)
                        }
                        else -> {
                            val regex = buildString(pattern.length * 2) {
                                append('^')
                                pattern.forEach { ch ->
                                    when (ch) {
                                        '*' -> append(".*")
                                        '.', '(', ')', '[', ']', '{', '}', '+', '?', '^', '$', '|', '\\' -> {
                                            append('\\')
                                            append(ch)
                                        }
                                        else -> append(ch)
                                    }
                                }
                                append('$')
                            }.toRegex()
                            wildcards += CompiledWildcardRule(pattern, regex, type)
                        }
                    }
                }
            }

            prefixes.sortByDescending { it.prefix.length }
            return WeaponAnimationMatcher(defaultType, exact, prefixes, wildcards)
        }
    }
}

private data class CompiledPrefixRule(
    val rawPattern: String,
    val prefix: String,
    val type: WeaponAnimationType
)

private data class CompiledWildcardRule(
    val rawPattern: String,
    val regex: Regex,
    val type: WeaponAnimationType
)
