package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import org.bukkit.World
import java.util.*

class MockBukkitWrapper : BukkitWrapper {

    override fun getWorld(uuid: UUID): World {
        return MockWorld()
    }
}