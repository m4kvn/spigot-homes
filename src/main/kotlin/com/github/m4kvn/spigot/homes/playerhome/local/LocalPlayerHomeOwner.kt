package com.github.m4kvn.spigot.homes.playerhome.local

import org.jetbrains.exposed.dao.id.LongIdTable

object LocalPlayerHomeOwner : LongIdTable() {
    val ownerUUID = varchar(name = "owner_uuid", length = 50)
    val ownerName = varchar(name = "owner_name", length = 50)
}