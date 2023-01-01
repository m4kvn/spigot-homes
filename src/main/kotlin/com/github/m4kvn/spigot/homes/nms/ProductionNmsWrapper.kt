package com.github.m4kvn.spigot.homes.nms

import org.bukkit.World
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent

class ProductionNmsWrapper : NmsWrapper {

    override fun newDisplayEntity(
        world: World,
        text: String,
        isVisible: Boolean,
        location: DisplayEntityLocation
    ): DisplayEntity {
        val worldServer = (world as CraftWorld).handle
        val entity = CustomEntityArmorStand(
            world = worldServer,
            locationX = location.x,
            locationY = location.y,
            locationZ = location.z,
            text = text,
        )
        worldServer.addFreshEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM)
        return entity
    }
}