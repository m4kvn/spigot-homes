package com.github.m4kvn.spigot.homes.nms

import net.minecraft.world.entity.decoration.EntityArmorStand
import org.bukkit.craftbukkit.v1_19_R1.CraftServer
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftArmorStand
import org.bukkit.entity.Entity

@Suppress("unused")
class CustomCraftArmorStand(
    craftServer: CraftServer,
    entityArmorStand: EntityArmorStand,
    text: String,
) : CraftArmorStand(craftServer, entityArmorStand), DisplayEntity {

    override fun dead() = remove()
    override val customText: String = text
    override val isAlive: Boolean
        get() = !isDead

    override fun damage(amount: Double, source: Entity?) {}
    override fun isInvulnerable(): Boolean = true
    override fun setInvulnerable(flag: Boolean) {}
    override fun isCollidable(): Boolean = false
    override fun setCollidable(collidable: Boolean) {}
    override fun isSmall(): Boolean = true
    override fun setSmall(small: Boolean) {}
    override fun isMarker(): Boolean = false
    override fun setMarker(marker: Boolean) {}
    override fun hasGravity(): Boolean = false
    override fun setGravity(gravity: Boolean) {}
    override fun hasArms(): Boolean = false
    override fun setArms(arms: Boolean) {}
    override fun hasBasePlate(): Boolean = false
    override fun setBasePlate(basePlate: Boolean) {}
    override fun isVisible(): Boolean = false
    override fun setVisible(visible: Boolean) {}
    override fun isCustomNameVisible(): Boolean = true
    override fun setCustomNameVisible(flag: Boolean) {}
    override fun getCustomName(): String = customText
    override fun setCustomName(name: String?) {}
}