package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.TemporaryPlayerHomeManager
import org.bukkit.entity.Player

class GetTemporaryPlayerHomeUseCase(
    private val temporaryPlayerHomeManager: TemporaryPlayerHomeManager,
) : UseCase {

    operator fun invoke(player: Player): PlayerHome.Temporary? {
        return temporaryPlayerHomeManager.get(player.uniqueId)
    }
}