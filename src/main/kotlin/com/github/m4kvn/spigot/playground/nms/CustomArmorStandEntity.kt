package com.github.m4kvn.spigot.playground.nms

import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.EntityArmorStand
import net.minecraft.world.level.World
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent

class CustomArmorStandEntity(
    private val world: World,
    positionX: Double,
    positionY: Double,
    positionZ: Double,
) : EntityArmorStand(world, positionX, positionY, positionZ) {

    constructor(player: Player) : this(
        world = (player.world as CraftWorld).handle,
        positionX = player.location.x,
        positionY = player.location.y,
        positionZ = player.location.z,
    )

    override fun a(entity_removalreason: RemovalReason?) {
        // super.a(entity_removalreason)
    }

    fun dead() {
        removeEntity()
    }

    fun spawn() {
        world.addFreshEntity(this, CreatureSpawnEvent.SpawnReason.DEFAULT)
    }

    fun setName(name: String) {
        setCustomName(name)
        showCustomName(true)
    }
}

fun Entity.removeEntity() {
    b(Entity.RemovalReason.e)
}

fun Entity.setCustomName(name: String) {
    val component = IChatBaseComponent.a(name)
    b(component)
}

fun Entity.showCustomName(isShow: Boolean) {
    n(isShow)
}