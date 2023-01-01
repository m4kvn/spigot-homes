package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.nms.DisplayEntity
import com.github.m4kvn.spigot.homes.nms.DisplayEntityLocation
import com.github.m4kvn.spigot.homes.nms.NmsWrapper
import org.bukkit.World

class MockNmsWrapper : NmsWrapper {

    override fun newDisplayEntity(
        world: World,
        text: String,
        isVisible: Boolean,
        location: DisplayEntityLocation,
    ): DisplayEntity = object : DisplayEntity {
        private var isDead = false
        override val isAlive: Boolean = !isDead
        override val customText: String = text
        override fun dead() { isDead = true }
    }
}