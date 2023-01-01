package com.github.m4kvn.spigot.homes.nms

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.decoration.EntityArmorStand
import net.minecraft.world.level.World
import org.bukkit.craftbukkit.v1_19_R1.util.CraftChatMessage

class CustomEntityArmorStand(
    world: World,
    locationX: Double,
    locationY: Double,
    locationZ: Double,
    private val text: String,
) : EntityArmorStand(world, locationX, locationY, locationZ), DisplayEntity {

    init {
        setInvulnerable()
        setSmall()
        setMarker()
        setArms()
        setBasePlate()
        setVisible()
        setCustomNameVisible()
        setCustomName()
        setCollidable()
        setGravity()
    }

    override val customText: String
        get() = text

    override val isAlive: Boolean
        get() = !isDead()

    override fun dead() = remove()
    override fun a(damagesource: DamageSource?, f: Float): Boolean = damage()
    private fun isDead() = !super.bp()
    private fun remove() = super.ah()
    private fun damage() = false
    private fun setInvulnerable() = m(true)
    private fun setSmall() = a(true)
    private fun setMarker() = t(true)
    private fun setArms() = r(false)
    private fun setBasePlate() = s(true)
    private fun setVisible() = j(true)
    private fun setCustomNameVisible() = n(true)
    private fun setCustomName() = b(CraftChatMessage.fromStringOrNull(customText))

    private fun setCollidable() {
        super.collides = false
    }

    private fun setGravity() {
        super.Q = true
        e(true)
    }
}