package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import org.bukkit.entity.Player

class RemoveDefaultHomeUseCase(
    private val displayManager: DisplayEntityManager,
    private val homeManager: PlayerHomeManager,
) : UseCase {

    operator fun invoke(player: Player): PlayerHome? {
        val response = homeManager.removeDefaultHome(player.uniqueId)
        if (response !is PlayerHomeManager.Response.Success) return null
        displayManager.despawnEntities(response.playerHome)
        return response.playerHome
    }
}