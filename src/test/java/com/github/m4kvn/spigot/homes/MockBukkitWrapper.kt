package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.scheduler.BukkitScheduler
import org.mockito.kotlin.mock
import java.util.*

class MockBukkitWrapper : BukkitWrapper {
    private val worldMap = hashMapOf<UUID, MockWorld>()

    override fun getWorld(uuid: UUID): World? {
        return worldMap[uuid]
    }

    override fun getLocation(playerHome: PlayerHome): Location {
        val world = worldMap[playerHome.location.worldUUID] ?: newMockWorld()
        val homeChunk = playerHome.location.chunk
        val chunk = world.getChunkAt(homeChunk.x, homeChunk.z)
        return world.newMockLocation(
            locationChunk = chunk,
            locationX = playerHome.location.locationX,
            locationY = playerHome.location.locationY,
            locationZ = playerHome.location.locationZ,
            locationPitch = playerHome.location.locationPitch,
            locationYaw = playerHome.location.locationYaw,
        )
    }

    override fun getScheduler(): BukkitScheduler {
        return mock()
    }

    fun newMockWorld(
        worldUUID: UUID = UUID.randomUUID(),
        worldName: String = "mock_world_${worldUUID}",
    ): MockWorld {
        val world = MockWorld(
            uuid = worldUUID,
            name = worldName,
        )
        worldMap[worldUUID] = world
        return world
    }
}