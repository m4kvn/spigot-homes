package com.github.m4kvn.spigot.homes.bukkit

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.util.*

class ProductionBukkitWrapper : BukkitWrapper {

    override fun getWorld(uuid: UUID): World? {
        return Bukkit.getWorld(uuid)
    }

    override fun getLocation(playerHome: PlayerHome): Location {
        return Location(
            getWorld(playerHome.location.worldUUID),
            playerHome.location.locationX,
            playerHome.location.locationY,
            playerHome.location.locationZ,
            playerHome.location.locationYaw,
            playerHome.location.locationPitch,
        )
    }
}