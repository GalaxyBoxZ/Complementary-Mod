package com.gbz.combat.plugin.command

import com.gbz.combat.plugin.GbzCombatPlugin
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class CombatCommand(private val plugin: GbzCombatPlugin) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.YELLOW}/combat <debug|reload>")
            return true
        }

        when (args[0].lowercase()) {
            "debug" -> {
                val config = plugin.configService.current
                sender.sendMessage("${ChatColor.AQUA}GBZ Combat Debug")
                sender.sendMessage("${ChatColor.GRAY}moddedPlayers=${plugin.modDetectionManager.getModdedPlayerCount()}")
                sender.sendMessage("${ChatColor.GRAY}packetDistance=${config.packetDistance}")
                sender.sendMessage("${ChatColor.GRAY}animationSpeed=${config.animationSpeed}")
                sender.sendMessage("${ChatColor.GRAY}smoothTransitions=${config.smoothTransitions}")
                sender.sendMessage("${ChatColor.GRAY}firstPerson=${config.firstPersonEnabled}")
                return true
            }

            "reload" -> {
                plugin.configService.reload()
                sender.sendMessage("${ChatColor.GREEN}GBZ Combat config reloaded.")
                return true
            }
        }

        sender.sendMessage("${ChatColor.RED}Unknown subcommand.")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String> {
        return if (args.size == 1) listOf("debug", "reload").filter { it.startsWith(args[0], ignoreCase = true) } else emptyList()
    }
}
