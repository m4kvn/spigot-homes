package com.github.m4kvn.spigot.homes.messenger

import org.bukkit.ChatColor

fun sendConsole(block: StringBuilder.() -> Unit) {
    HomesMessenger.send(buildString(block))
}

fun sendConsoleMessage(block: Message.() -> String) {
    sendConsole { appendMessage(block) }
}

fun StringBuilder.appendMessage(color: ChatColor, block: Message.() -> String): StringBuilder = apply {
    append("$color")
    append(block(Message))
    append("${ChatColor.RESET}")
}

fun StringBuilder.appendMessage(block: Message.() -> String): StringBuilder = apply {
    append(block(Message))
}