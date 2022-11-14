package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.command.HomeSetCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent

class HomesCommendExecutor : CommandExecutor, KoinComponent {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) return false
        val updatedArgs = args.drop(1)
        return when (args[0]) {
            "set" -> HomeSetCommand().execute(sender, updatedArgs)
            else -> false
        }
    }
}