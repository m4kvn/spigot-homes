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
        location: DisplayEntityLocation
    ): DisplayEntity = object : DisplayEntity {
        override var text: String? = text
        override var isVisible: Boolean = isVisible
        override var location: DisplayEntityLocation = location
        override var isDead: Boolean = true
    }
}