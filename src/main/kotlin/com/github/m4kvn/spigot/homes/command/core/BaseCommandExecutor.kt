package com.github.m4kvn.spigot.homes.command.core

import com.github.m4kvn.spigot.homes.messenger.appends
import com.github.m4kvn.spigot.homes.messenger.send
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseCommandExecutor : CommandExecutor, TabCompleter, KoinComponent {
    private val plugin: JavaPlugin by inject()

    abstract val commandName: String
    abstract val subCommands: List<SubCommandExecutor>

    private val flags: List<String>
        get() = subCommands.flatMap { it.flags }

    abstract fun onCommand(
        sender: CommandSender,
        args: Array<out String>,
    ) : CommandResponse

    abstract fun onTabComplete(
        sender: CommandSender,
        args: Array<out String>,
        index: Int? = null,
    ): List<String>

    fun register() {
        val command = plugin.getCommand(commandName) ?: return
        command.setExecutor(this)
        command.tabCompleter = this
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        val flag = args.firstOrNull()
        val subCommand = findSubCommand(flag)
        val response = subCommand
            ?.onCommand(sender, args.drop(1))
            ?: onCommand(sender, args)
        if (response is CommandResponse.Failed) {
            val message = subCommand
                ?.createUsage(label, flag)
                ?: command.createUsage(label)
            sender.send {
                appends(ChatColor.RED) { "${response.message}\n" }
                appends { "Usage: " }
                appends(ChatColor.YELLOW) { message }
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): MutableList<String> {
        return getCompletions(sender, args).toMutableList()
    }

    private fun getCompletions(
        sender: CommandSender,
        args: Array<out String>,
    ): List<String> {
        val firstArgument = args.firstOrNull() ?: return onTabComplete(sender, args)
        val completions = if (args.lastIndex == 0) {
            if (firstArgument.isFlagPrefix)
                flags else
                onTabComplete(sender, args)
        } else {
            findSubCommand(firstArgument)
                ?.onTabComplete(sender, args.drop(1))
                ?: onTabComplete(sender, args)
        }
        val regex = "^${args.last()}.*".toRegex()
        return completions.filter { it.matches(regex) }
    }

    private fun findSubCommand(flag: String?): SubCommandExecutor? {
        return subCommands.find { it.flags.contains(flag) }
    }

    private fun Command.createUsage(label: String): String {
        return usage.replace("<command>", label)
    }

    private val String.isFlagPrefix: Boolean
        get() = startsWith("-")
}