package com.github.m4kvn.spigot.homes.nms

import org.bukkit.World

interface NmsWrapper {

    fun newDisplayEntity(
        world: World,
        text: String,
        isVisible: Boolean,
        location: DisplayEntityLocation,
    ): DisplayEntity
}