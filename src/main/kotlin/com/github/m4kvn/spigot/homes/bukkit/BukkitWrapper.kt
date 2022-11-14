package com.github.m4kvn.spigot.homes.bukkit

import org.bukkit.World
import java.util.*

interface BukkitWrapper {
    fun getWorld(uuid: UUID): World?
}