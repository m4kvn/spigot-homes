package com.github.m4kvn.spigot.homes.nms

import org.bukkit.World
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld

class ProductionNmsWrapper : NmsWrapper {

    override fun newDisplayEntity(
        world: World,
        text: String,
        isVisible: Boolean,
        location: DisplayEntityLocation
    ): DisplayEntity {
        val worldServer = (world as CraftWorld).handle
        return CustomEntityArmorStand(worldServer).also {
            it.text = text
            it.isVisible = isVisible
            it.location = location
        }
    }
}