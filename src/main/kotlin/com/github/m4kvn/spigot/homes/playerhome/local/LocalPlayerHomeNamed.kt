package com.github.m4kvn.spigot.homes.playerhome.local

import org.jetbrains.exposed.dao.id.LongIdTable

object LocalPlayerHomeNamed : LongIdTable() {
    val ownerUUID = varchar(name = "owner_uuid", length = 50).index()
    val ownerName = varchar(name = "owner_name", length = 50)
    val worldUUID = varchar(name = "world_uuid", length = 50)
    val worldName = varchar(name = "world_name", length = 50)
    val chunkX = integer(name = "chunk_x")
    val chunkZ = integer(name = "chunk_z")
    val locationX = double(name = "location_x")
    val locationY = double(name = "location_y")
    val locationZ = double(name = "location_z")
    val isPrivate = bool(name = "is_private")
    val homeName = varchar(name = "home_name", length = 50)
    val locationYaw = float(name = "location_yaw").default(0f)
    val locationPitch = float(name = "location_pitch").default(0f)
}