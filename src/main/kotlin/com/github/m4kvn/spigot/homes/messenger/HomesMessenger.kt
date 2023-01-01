package com.github.m4kvn.spigot.homes.messenger

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object HomesMessenger : KoinComponent {
    private val messenger by inject<Messenger>()

    fun send(message: String) {
        messenger.sendConsoleMessage(message)
    }
}