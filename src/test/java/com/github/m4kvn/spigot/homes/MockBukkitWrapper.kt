package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import org.bukkit.Location
import org.bukkit.World
import java.util.*

class MockBukkitWrapper : BukkitWrapper {
    private val worldMap = hashMapOf<UUID, World>()

    override fun getWorld(uuid: UUID): World {
        val world = MockWorld(uuid = uuid)
        worldMap[uuid] = world
        return world
    }

    override fun getLocation(playerHome: PlayerHome): Location {
        return Location(
            getWorld(playerHome.location.worldUUID),
            playerHome.location.locationX,
            playerHome.location.locationY,
            playerHome.location.locationZ,
        )
    }
}