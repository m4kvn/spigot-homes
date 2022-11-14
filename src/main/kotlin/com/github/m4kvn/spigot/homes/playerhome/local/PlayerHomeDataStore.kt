package com.github.m4kvn.spigot.homes.playerhome.local

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeOwner
import org.jetbrains.exposed.sql.Database

interface PlayerHomeDataStore {
    var database: Database?
    fun connectDatabase()
    fun disconnectDatabase()
    fun createTables()
    fun storeOwnerList(ownerList: List<PlayerHomeOwner>)
    fun restoreOwnerList(): List<PlayerHomeOwner>
    fun storeDefaultHomeList(playerHomeList: List<PlayerHome.Default>)
    fun restoreDefaultHomeList(): List<PlayerHome.Default>
    fun storeNamedHomeList(playerHomeList: List<PlayerHome.Named>)
    fun restoreNamedHomeList(ownerUUID: String): List<PlayerHome.Named>
}