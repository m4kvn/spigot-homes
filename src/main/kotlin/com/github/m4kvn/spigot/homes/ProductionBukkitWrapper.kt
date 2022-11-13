package com.github.m4kvn.spigot.homes

import org.bukkit.Bukkit
import org.bukkit.World
import java.util.*

class ProductionBukkitWrapper : BukkitWrapper {

    override fun getWorld(uuid: UUID): World? {
        return Bukkit.getWorld(uuid)
    }
}