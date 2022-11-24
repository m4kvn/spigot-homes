package com.github.m4kvn.spigot.homes

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.mockito.kotlin.mock
import java.util.*

class MockPlayer(
    initialWorld: World,
    initialLocation: Location,
    private val playerUUID: UUID,
    private val playerName: String,
) : Player by mock() {
    private var world: World = initialWorld
    private var location: Location = initialLocation

    override fun getWorld(): World = world

    override fun getName(): String = playerName

    override fun getUniqueId(): UUID = playerUUID

    override fun getLocation(): Location = location

    override fun teleport(
        newLocation: Location,
        cause: PlayerTeleportEvent.TeleportCause,
    ): Boolean {
        newLocation.world?.let { world = it }
        location = newLocation
        return true
    }
}