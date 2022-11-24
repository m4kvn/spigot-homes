package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

class TeleportPlayerHomeUseCase(
    private val bukkit: BukkitWrapper,
    private val createTemporaryPlayerHomeUseCase: CreateTemporaryPlayerHomeUseCase,
    private val saveTemporaryPlayerHomeUseCase: SaveTemporaryPlayerHomeUseCase,
) : UseCase {

    operator fun invoke(
        player: Player,
        playerHome: PlayerHome,
        savePrevious: Boolean = true,
    ) {
        val previousPlayerHome = if (savePrevious) createTemporaryPlayerHomeUseCase(player) else null
        val location = bukkit.getLocation(playerHome)
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
        if (previousPlayerHome != null) {
            saveTemporaryPlayerHomeUseCase(previousPlayerHome)
        }
    }
}