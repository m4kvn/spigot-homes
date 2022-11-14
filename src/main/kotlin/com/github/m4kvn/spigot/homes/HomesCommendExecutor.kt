package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.command.HomeCommand
import com.github.m4kvn.spigot.homes.command.HomeSetCommand
import com.github.m4kvn.spigot.homes.command.SubCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent

class HomesCommendExecutor : CommandExecutor, KoinComponent {
    private val mainCommand = HomeCommand()
    private val subCommandMap = hashMapOf<String, SubCommand>(
        "set" to HomeSetCommand(),
    )

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) {
            mainCommand.execute(sender, args.toList())
            return true
        }
        val subCommand = subCommandMap[args[0]]
        if (subCommand != null) {
            val response = subCommand.execute(sender, args.drop(1))
            if (response is SubCommand.Response.Failed) {
                sender.sendMessage(response.message)
            }
            return true
        }
        return false
    }
}