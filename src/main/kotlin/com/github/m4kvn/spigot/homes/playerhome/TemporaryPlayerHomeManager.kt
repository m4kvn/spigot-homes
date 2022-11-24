package com.github.m4kvn.spigot.homes.playerhome

import java.util.*

class TemporaryPlayerHomeManager {
    private val temporaryHomeMap = hashMapOf<UUID, PlayerHome.Temporary>()

    fun add(playerHome: PlayerHome.Temporary) {
        temporaryHomeMap[playerHome.owner.playerUUID] = playerHome
    }

    fun get(playerUUID: UUID): PlayerHome.Temporary? {
        return temporaryHomeMap[playerUUID]
    }
}