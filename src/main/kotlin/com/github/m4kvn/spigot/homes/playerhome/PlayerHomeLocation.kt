package com.github.m4kvn.spigot.homes.playerhome

import java.util.*

data class PlayerHomeLocation(
    val worldUUID: UUID,
    val worldName: String,
    val locationX: Double,
    val locationY: Double,
    val locationZ: Double,
    val locationYaw: Float,
    val locationPitch: Float,
    val chunk: PlayerHomeChunk,
)