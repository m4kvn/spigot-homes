package com.github.m4kvn.spigot.homes

import org.bukkit.World
import java.util.*

class MockBukkitWrapper : BukkitWrapper {

    override fun getWorld(uuid: UUID): World {
        return MockWorld()
    }
}