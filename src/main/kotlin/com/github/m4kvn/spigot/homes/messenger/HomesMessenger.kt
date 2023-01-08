package com.github.m4kvn.spigot.homes.messenger

import org.bukkit.command.CommandSender
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object HomesMessenger : KoinComponent {
    private val messenger by inject<Messenger>()

    fun send(message: String) {
        messenger.sendConsoleMessage(message)
    }

    fun send(sender: CommandSender, message: String) {
        messenger.sendPrefixMessage(sender, message)
    }
}