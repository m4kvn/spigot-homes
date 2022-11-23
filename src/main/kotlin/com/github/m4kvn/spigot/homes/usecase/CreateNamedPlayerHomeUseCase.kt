package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeOwner
import org.bukkit.entity.Player

class CreateNamedPlayerHomeUseCase : UseCase {

    operator fun invoke(player: Player, homeName: String): PlayerHome.Named {
        return PlayerHome.Named(
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
                locationPitch = player.location.pitch,
                locationYaw = player.location.yaw,
                chunkX = player.location.chunk.x,
                chunkZ = player.location.chunk.z,
            ),
            name = homeName,
        )
    }
}