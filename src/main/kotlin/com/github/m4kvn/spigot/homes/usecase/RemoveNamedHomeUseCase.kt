package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import org.bukkit.entity.Player

class RemoveNamedHomeUseCase(
    private val displayManager: DisplayEntityManager,
    private val homeManager: PlayerHomeManager,
) : UseCase {

    operator fun invoke(player: Player, homeName: String): PlayerHome? {
        val response = homeManager.removeNamedHome(player.uniqueId, homeName)
        if (response !is PlayerHomeManager.Response.Success) return null
        displayManager.removeEntities(response.playerHome)
        return response.playerHome
    }
}