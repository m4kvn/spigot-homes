package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import org.bukkit.entity.Player

class SetDefaultPlayerHomeUseCase(
    private val displayManager: DisplayEntityManager,
    private val homeManager: PlayerHomeManager,
    private val createDefaultPlayerHomeUseCase: CreateDefaultPlayerHomeUseCase,
) : UseCase {

    operator fun invoke(player: Player): PlayerHome.Default {
        val newPlayerHome = createDefaultPlayerHomeUseCase(player)
        val addResponse = homeManager.addDefaultHome(newPlayerHome)
        if (addResponse is PlayerHomeManager.Response.DefaultHomeAlreadyExists) {
            displayManager.removeEntities(addResponse.currentPlayerHome)
            homeManager.replaceDefaultHome(newPlayerHome)
        }
        displayManager.addEntities(player.world, newPlayerHome)
        return newPlayerHome
    }
}