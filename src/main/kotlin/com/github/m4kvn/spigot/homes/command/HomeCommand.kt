package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.usecase.TeleportPlayerHomeUseCase
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomeCommand : SubCommand {
    private val teleportPlayerHomeUseCase by inject<TeleportPlayerHomeUseCase>()
    private val homeManager by inject<PlayerHomeManager>()

    override fun execute(player: Player, args: List<String>): SubCommand.Response {
        return if (args.isNotEmpty())
            teleportNamed(player, args[0]) else
            teleportDefault(player)
    }

    private fun teleportDefault(player: Player): SubCommand.Response {
        val playerHome = homeManager.getDefaultHome(player.uniqueId)
        if (playerHome == null) {
            val message = "You have not yet set default home."
            return SubCommand.Response.Failed(message)
        }
        teleportPlayerHomeUseCase(player, playerHome)
        return SubCommand.Response.Success
    }

    private fun teleportNamed(player: Player, homeName: String): SubCommand.Response {
        val playerHome = homeManager.getNamedHome(player.uniqueId, homeName)
        if (playerHome == null) {
            val message = "You have not yet set home named <$homeName>."
            return SubCommand.Response.Failed(message)
        }
        teleportPlayerHomeUseCase(player, playerHome)
        return SubCommand.Response.Success
    }
}