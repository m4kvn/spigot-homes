package com.github.m4kvn.spigot.homes.messenger

import org.bukkit.command.CommandSender

interface Messenger {
    fun sendPrefixMessage(sender: CommandSender, message: String)
    fun sendConsoleMessage(message: String)
}