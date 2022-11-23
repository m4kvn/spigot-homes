package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeOwner
import org.bukkit.entity.Player

class CreateDefaultPlayerHomeUseCase : UseCase {

    operator fun invoke(player: Player): PlayerHome.Default {
        return PlayerHome.Default(
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
                locationYaw = player.location.yaw,
                locationPitch = player.location.pitch,
                chunkX = player.location.chunk.x,
                chunkZ = player.location.chunk.z,
            ),
        )
    }
}