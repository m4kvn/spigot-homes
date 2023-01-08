package com.github.m4kvn.spigot.homes.messenger

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

fun sendConsole(block: StringBuilder.() -> Unit) {
    HomesMessenger.send(buildString(block))
}

fun sendConsoleMessage(block: Message.() -> String) {
    sendConsole { appendMessage(block) }
}

fun CommandSender.send(block: StringBuilder.() -> Unit) {
    HomesMessenger.send(this, buildString(block))
}

fun CommandSender.sendMessage(block: Message.() -> String) {
    send { appendMessage(block) }
}

fun StringBuilder.appendMessage(color: ChatColor, block: Message.() -> String): StringBuilder = apply {
    append("$color")
    append(block(Message))
    append("${ChatColor.RESET}")
}

fun StringBuilder.appends(color: ChatColor, block: Message.() -> String): StringBuilder = apply {
    append("$color")
    append(block(Message))
    append("${ChatColor.RESET}")
}

fun StringBuilder.appendMessage(block: Message.() -> String): StringBuilder = apply {
    append(block(Message))
}

fun StringBuilder.appends(block: Message.() -> String): StringBuilder = apply {
    append(block(Message))
}