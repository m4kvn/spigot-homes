package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.model.PlayerHome
import com.github.m4kvn.spigot.homes.model.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.model.PlayerHomeOwner
import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomesCommendExecutor : CommandExecutor, KoinComponent {
    private val manager by inject<DisplayEntityManager>()

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) return false
        return when (args[0]) {
            "set" -> onSetCommand(sender, args.drop(1))
            else -> false
        }
    }

    private fun onSetCommand(
        player: Player,
        args: List<String>,
    ): Boolean {
        return if (args.isNotEmpty())
            spawnNamed(player, args[0]) else
            spawnDefault(player)
    }

    private fun spawnDefault(player: Player): Boolean {
        val playerHome = PlayerHome.Default(
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
        manager.spawnEntities(player.world, playerHome)
        return true
    }

    private fun spawnNamed(player: Player, homeName: String): Boolean {
        val playerHome = PlayerHome.Named(
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
        manager.spawnEntities(player.world, playerHome)
        return true
    }
}