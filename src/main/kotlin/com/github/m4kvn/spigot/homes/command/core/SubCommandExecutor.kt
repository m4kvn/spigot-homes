package com.github.m4kvn.spigot.homes.command.core

import org.bukkit.command.CommandSender
import org.koin.core.component.KoinComponent

abstract class SubCommandExecutor : KoinComponent {
    abstract val flags: List<String>
    abstract val usage: String

    abstract fun onCommand(
        sender: CommandSender,
        args: List<String>,
    ): CommandResponse

    abstract fun onTabComplete(
        sender: CommandSender,
        args: List<String>,
    ): List<String>

    fun createUsage(label: String, flag: String?): String {
        return buildString {
            append("/$label")
            if (flag != null) {
                append(" $flag")
            }
            append(" $usage")
        }
    }
}