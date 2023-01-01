package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import org.bukkit.entity.Player

class SetNamedPlayerHomeUseCase(
    private val displayManager: DisplayEntityManager,
    private val homeManager: PlayerHomeManager,
    private val createNamedPlayerHomeUseCase: CreateNamedPlayerHomeUseCase,
) : UseCase {

    operator fun invoke(player: Player, homeName: String): PlayerHome.Named {
        val newPlayerHome = createNamedPlayerHomeUseCase(player, homeName)
        val addResponse = homeManager.addNamedHome(newPlayerHome)
        if (addResponse is PlayerHomeManager.Response.NamedHomeAlreadyExists) {
            val currentPlayerHome = addResponse.currentPlayerHome
            displayManager.despawnEntities(currentPlayerHome)
            homeManager.replaceNamedHome(newPlayerHome)
        }
        displayManager.spawnEntities(player.world, newPlayerHome)
        return newPlayerHome
    }
}