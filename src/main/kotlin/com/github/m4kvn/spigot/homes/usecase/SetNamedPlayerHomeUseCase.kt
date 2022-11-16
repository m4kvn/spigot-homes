package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeOwner
import org.bukkit.entity.Player

class SetNamedPlayerHomeUseCase(
    private val displayManager: DisplayEntityManager,
    private val homeManager: PlayerHomeManager,
) : UseCase {

    operator fun invoke(player: Player, homeName: String) {
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
            val currentPlayerHome = addResponse.currentPlayerHome
            displayManager.removeEntities(currentPlayerHome)
            homeManager.replaceNamedHome(newPlayerHome)
        }
        displayManager.addEntities(player.world, newPlayerHome)
    }
}