package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeOwner
import org.bukkit.entity.Player

class SetDefaultPlayerHomeUseCase(
    private val displayManager: DisplayEntityManager,
    private val homeManager: PlayerHomeManager,
) : UseCase {

    operator fun invoke(player: Player) {
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
            displayManager.removeEntities(addResponse.currentPlayerHome)
            homeManager.replaceDefaultHome(newPlayerHome)
        }
        displayManager.addEntities(player.world, newPlayerHome)
    }
}