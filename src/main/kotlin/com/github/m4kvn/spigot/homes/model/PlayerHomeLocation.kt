package com.github.m4kvn.spigot.homes.model

import java.util.*

data class PlayerHomeLocation(
    val worldUUID: UUID,
    val worldName: String,
    val locationX: Double,
    val locationY: Double,
    val locationZ: Double,
    val chunkX: Int,
    val chunkZ: Int,
)