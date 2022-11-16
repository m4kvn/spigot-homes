package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomeRemoveSubCommand : SubCommand {
    private val displayManager by inject<DisplayEntityManager>()
    private val homeManager by inject<PlayerHomeManager>()

    override fun execute(player: Player, args: List<String>): SubCommand.Response {
        return if (args.isNotEmpty())
            removeNamedHome(player, args[0]) else
            removeDefaultHome(player)
    }

    private fun removeDefaultHome(player: Player): SubCommand.Response {
        val response = homeManager.removeDefaultHome(player.uniqueId)
        if (response is PlayerHomeManager.Response.Success) {
            displayManager.despawnEntities(response.playerHome)
            return SubCommand.Response.Success
        }
        val message = "Your default home was not found."
        return SubCommand.Response.Failed(message)
    }

    private fun removeNamedHome(player: Player, homeName: String): SubCommand.Response {
        val response = homeManager.removeNamedHome(player.uniqueId, homeName)
        if (response is PlayerHomeManager.Response.Success) {
            displayManager.despawnEntities(response.playerHome)
            return SubCommand.Response.Success
        }
        val message = "Your home named <$homeName> was not found."
        return SubCommand.Response.Failed(message)
    }
}