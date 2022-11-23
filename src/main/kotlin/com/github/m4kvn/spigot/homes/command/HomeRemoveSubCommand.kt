package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.usecase.RemoveDefaultHomeUseCase
import com.github.m4kvn.spigot.homes.usecase.RemoveNamedHomeUseCase
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomeRemoveSubCommand : SubCommand {
    private val removeDefaultHomeUseCase by inject<RemoveDefaultHomeUseCase>()
    private val removeNamedHomeUseCase by inject<RemoveNamedHomeUseCase>()

    override fun execute(player: Player, args: List<String>): SubCommand.Response {
        return if (args.isNotEmpty())
            removeNamedHome(player, args[0]) else
            removeDefaultHome(player)
    }

    private fun removeDefaultHome(player: Player): SubCommand.Response {
        val result = removeDefaultHomeUseCase(player)
        if (result == null) {
            val message = "Your default home was not found."
            return SubCommand.Response.Failed(message)
        }
        return SubCommand.Response.Success
    }

    private fun removeNamedHome(player: Player, homeName: String): SubCommand.Response {
        val result = removeNamedHomeUseCase(player, homeName)
        if (result == null) {
            val message = "Your home named <$homeName> was not found."
            return SubCommand.Response.Failed(message)
        }
        return SubCommand.Response.Success
    }
}