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
    private val aliasMap = hashMapOf(
        "-s" to "--set",
        "-r" to "--remove",
        "-l" to "--list",
        "-p" to "--previous",
    )

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) return false
        if (args.isNotEmpty()) {
            val flag = aliasMap[args[0]] ?: args[0]
            val subCommand = subCommandMap[flag]
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