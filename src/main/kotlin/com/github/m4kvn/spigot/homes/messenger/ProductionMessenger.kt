package com.github.m4kvn.spigot.homes.messenger

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class ProductionMessenger(
    private val plugin: JavaPlugin,
) : Messenger {

    override fun sendPrefixMessage(sender: CommandSender, message: String) {
        sender.sendMessage("${ChatColor.AQUA}[${plugin.name}]${ChatColor.RESET} $message")
    }

    override fun sendConsoleMessage(message: String) {
        sendPrefixMessage(plugin.server.consoleSender, message)
    }
}