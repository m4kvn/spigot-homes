package com.github.m4kvn.spigot.homes.command.core

import com.github.m4kvn.spigot.homes.messenger.Message

sealed class CommandResponse {
    object Success : CommandResponse()

    data class Failed(
        private val block: Message.() -> String,
    ) : CommandResponse() {
        val message: String
            get() = block(Message)
    }
}