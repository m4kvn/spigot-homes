package com.github.m4kvn.spigot.homes.bukkit

import com.github.m4kvn.spigot.homes.model.PlayerHome
import org.bukkit.Location
import org.bukkit.World
import java.util.*

interface BukkitWrapper {
    fun getWorld(uuid: UUID): World?
    fun getLocation(playerHome: PlayerHome): Location
}