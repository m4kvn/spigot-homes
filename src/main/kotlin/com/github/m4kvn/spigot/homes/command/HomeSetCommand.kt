package com.github.m4kvn.spigot.homes.command

import com.github.m4kvn.spigot.homes.PlayerHomeManager
import com.github.m4kvn.spigot.homes.model.PlayerHome
import com.github.m4kvn.spigot.homes.model.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.model.PlayerHomeOwner
import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import org.bukkit.entity.Player
import org.koin.core.component.inject

class HomeSetCommand : SubCommand {
    private val displayManager by inject<DisplayEntityManager>()
    private val homeManager by inject<PlayerHomeManager>()

    override fun execute(player: Player, args: List<String>): SubCommand.Response {
        return if (args.isNotEmpty())
            spawnNamed(player, args[0]) else
            spawnDefault(player)
    }

    private fun spawnDefault(player: Player): SubCommand.Response {
        val newPlayerHome = PlayerHome.Default(
            owner = PlayerHomeOwner(
                playerUUID = player.uniqueId,
                playerName = player.name,
            ),
            location = PlayerHomeLocation(
                worldUUID = player.world.uid,
                worldName = player.world.name,
                locationX = player.location.x,
                locationY = player.location.y,
                locationZ = player.location.z,
                chunkX = player.location.chunk.x,
                chunkZ = player.location.chunk.z,
            ),
        )
        val addResponse = homeManager.addDefaultHome(newPlayerHome)
        if (addResponse is PlayerHomeManager.Response.DefaultHomeAlreadyExists) {
            displayManager.despawnEntities(player.world, addResponse.currentPlayerHome)
            homeManager.replaceDefaultHome(newPlayerHome)
        }
        displayManager.spawnEntities(player.world, newPlayerHome)
        return SubCommand.Response.Success
    }

    private fun spawnNamed(player: Player, homeName: String): SubCommand.Response {
        val newPlayerHome = PlayerHome.Named(
            owner = PlayerHomeOwner(
                playerUUID = player.uniqueId,
                playerName = player.name,
            ),
            location = PlayerHomeLocation(
                worldUUID = player.world.uid,
                worldName = player.world.name,
                locationX = player.location.x,
                locationY = player.location.y,
                locationZ = player.location.z,
                chunkX = player.location.chunk.x,
                chunkZ = player.location.chunk.z,
            ),
            name = homeName,
        )
        val addResponse = homeManager.addNamedHome(newPlayerHome)
        if (addResponse is PlayerHomeManager.Response.NamedHomeAlreadyExists) {
            displayManager.despawnEntities(player.world, addResponse.currentPlayerHome)
            homeManager.replaceNamedHome(newPlayerHome)
        }
        displayManager.spawnEntities(player.world, newPlayerHome)
        return SubCommand.Response.Success
    }
}