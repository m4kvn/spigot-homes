package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.messenger.HomesMessenger
import com.github.m4kvn.spigot.homes.messenger.Message
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeChunk
import org.bukkit.ChatColor
import org.bukkit.Chunk

inline val Chunk.asPlayerHomeChunk: PlayerHomeChunk
    get() = PlayerHomeChunk(x, z, world.uid)

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