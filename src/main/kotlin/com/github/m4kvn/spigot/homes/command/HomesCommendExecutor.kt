package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.messenger.Messenger
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomesCommendExecutor : CommandExecutor, KoinComponent {
    private val messenger by inject<Messenger>()
    private val mainCommand = HomeCommand()
    private val subCommandMap = hashMapOf(
        "--set" to HomeSetSubCommand(),
        "--remove" to HomeRemoveSubCommand(),
        "--list" to HomeListCommand(),
        "--previous" to HomePreviousCommand(),
    )

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) return false
        if (args.isNotEmpty()) {
            val subCommand = subCommandMap[args[0]]
            if (subCommand != null) {
                val response = subCommand.execute(sender, args.drop(1))
                if (response is SubCommand.Response.Failed) {
                    messenger.sendPrefixMessage(sender, response.message)
                }
                return true
            }
        }
        val response = mainCommand.execute(sender, args.toList())
        if (response is SubCommand.Response.Failed) {
            messenger.sendPrefixMessage(sender, response.message)
        }
        return true
    }
}