package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomeCommand : SubCommand {
    private val homeManager by inject<PlayerHomeManager>()
    private val bukkit by inject<BukkitWrapper>()

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
        val location = bukkit.getLocation(playerHome)
        player.teleport(location)
        return SubCommand.Response.Success
    }

    private fun teleportNamed(player: Player, homeName: String): SubCommand.Response {
        val playerHome = homeManager.getNamedHome(player.uniqueId, homeName)
        if (playerHome == null) {
            val message = "You have not yet set home named <$homeName>."
            return SubCommand.Response.Failed(message)
        }
        val location = bukkit.getLocation(playerHome)
        player.teleport(location)
        return SubCommand.Response.Success
    }
}