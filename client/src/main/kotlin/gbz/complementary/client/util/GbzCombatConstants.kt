package gbz.complementary.client.util

import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GBZCombatConstants {
    const val MOD_ID: String = "gbz"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    fun id(path: String): Identifier = Identifier.of(MOD_ID, path)
}
