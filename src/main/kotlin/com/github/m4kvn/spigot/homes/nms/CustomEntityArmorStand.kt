package com.github.m4kvn.spigot.homes.nms

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.sounds.SoundEffect
import net.minecraft.world.EnumHand
import net.minecraft.world.EnumInteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.EnumItemSlot
import net.minecraft.world.entity.decoration.EntityArmorStand
import net.minecraft.world.entity.player.EntityHuman
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.World
import net.minecraft.world.phys.Vec3D
import org.bukkit.event.entity.CreatureSpawnEvent

class CustomEntityArmorStand(
    private val world: World,
) : EntityArmorStand(EntityTypes.d, world), DisplayEntity {

    override var text: String?
        get() = Z()?.string
        set(value) {
            val component = IChatBaseComponent.a(value)
            super.b(component)
        }

    override var location: DisplayEntityLocation
        get() {
            val vec = cZ()
            return DisplayEntityLocation(vec.c, vec.d, vec.e)
        }
        set(value) {
            e(value.x, value.y, value.z)
        }

    override var isVisible: Boolean
        get() = cu()
        set(value) {
            super.n(value)
        }

    override var isDead: Boolean
        get() = du()
        set(value) {
            if (value) dead() else spawn()
        }

    init {
        a(true) // setSmall
        r(false) // setArms
        s(true) // setBasePlate
        t(true) // setMarker
        e(true) // setNoGravity
        j(true) // setInvisible
        collides = false
    }

    override fun a(nbttagcompound: NBTTagCompound?) {}

    override fun b(nbttagcompound: NBTTagCompound?) {}

    override fun d(nbttagcompound: NBTTagCompound?): Boolean = false

    override fun e(nbttagcompound: NBTTagCompound?): Boolean = false

    override fun g(nbttagcompound: NBTTagCompound?) {}

    // isInvulnerable
    override fun b(damagesource: DamageSource?): Boolean = true

    // isCollidable
    override fun bm(): Boolean = false

    // setCustomName
    override fun b(ichatbasecomponent: IChatBaseComponent?) {}

    // setCustomNameVisible
    override fun n(flag: Boolean) {}

    override fun a(
        entityHuman: EntityHuman,
        vec3D: Vec3D,
        enumHand: EnumHand,
    ): EnumInteractionResult {
        // EnumInteractionResult.PASS
        return EnumInteractionResult.d
    }

    override fun g(itemstack: ItemStack?): Boolean = false

    // setItemSlot
    override fun a(enumitemslot: EnumItemSlot?, itemstack: ItemStack?) {}

    override fun setItemSlot(enumitemslot: EnumItemSlot?, itemstack: ItemStack?, silent: Boolean) {}

    override fun inactiveTick() {}

    override fun a(soundEffect: SoundEffect, f: Float, f1: Float) {}

    override fun a(soundEffect: SoundEffect) {}

    // removeEntity(die)
    override fun a(entity_removalreason: RemovalReason?) {}

    private fun spawn() {
        world.addFreshEntity(this, CreatureSpawnEvent.SpawnReason.DEFAULT)
    }

    private fun dead() {
        b(RemovalReason.e)
    }
}