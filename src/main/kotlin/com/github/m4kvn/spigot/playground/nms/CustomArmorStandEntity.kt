package com.github.m4kvn.spigot.playground.nms

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
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent

class CustomArmorStandEntity(
    private val world: World,
) : EntityArmorStand(EntityTypes.d, world) {

    constructor(player: Player) : this(
        world = (player.world as CraftWorld).handle,
    ) {
        setPosition(
            x = player.location.x,
            y = player.location.y,
            z = player.location.z,
        )
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

    fun dead() {
        b(RemovalReason.e)
    }

    fun spawn() {
        world.addFreshEntity(this, CreatureSpawnEvent.SpawnReason.DEFAULT)
    }

    private fun setPosition(x: Double, y: Double, z: Double) {
        e(x, y, z)
    }

    fun setName(name: String) {
        val component = IChatBaseComponent.a(name)
        super.b(component)
        super.n(true)
    }
}