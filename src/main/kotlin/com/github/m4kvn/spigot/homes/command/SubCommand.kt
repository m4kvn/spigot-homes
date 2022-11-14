package com.github.m4kvn.spigot.homes.command

import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent

interface SubCommand : KoinComponent {

    fun execute(player: Player, args: List<String>): Response

    sealed class Response {
        object Success : Response()
        data class Failed(
            val message: String,
        ) : Response()
    }
}