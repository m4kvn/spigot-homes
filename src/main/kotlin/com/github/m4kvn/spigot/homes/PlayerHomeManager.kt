package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.model.PlayerHome
import java.util.*

class PlayerHomeManager {
    private val defaultHomeMap = hashMapOf<UUID, PlayerHome.Default>()
    private val namedHomeMap = hashMapOf<UUID, HashMap<String, PlayerHome.Named>>()

    fun addDefaultHome(playerHome: PlayerHome.Default): Response {
        if (defaultHomeMap.contains(playerHome.owner.playerUUID)) {
            return Response.DefaultHomeAlreadyExists
        }
        defaultHomeMap[playerHome.owner.playerUUID] = playerHome
        return Response.Success(playerHome)
    }

    fun addNamedHome(playerHome: PlayerHome.Named): Response {
        val namedHomes = namedHomeMap.getOrPut(playerHome.owner.playerUUID) { hashMapOf() }
        if (namedHomes.contains(playerHome.name)) {
            return Response.NamedHomeAlreadyExists
        }
        namedHomes[playerHome.name] = playerHome
        return Response.Success(playerHome)
    }

    fun getDefaultHome(ownerUUID: UUID): PlayerHome.Default? {
        return defaultHomeMap[ownerUUID]
    }

    fun getNamedHome(ownerUUID: UUID, homeName: String): PlayerHome.Named? {
        return namedHomeMap[ownerUUID]?.get(homeName)
    }

    fun removeDefaultHome(ownerUUID: UUID): Response {
        val playerHome = defaultHomeMap.remove(ownerUUID)
            ?: return Response.NotFoundDefaultHome
        return Response.Success(playerHome)
    }

    fun removeNamedHome(ownerUUID: UUID, homeName: String): Response {
        val playerHome = namedHomeMap[ownerUUID]?.remove(homeName)
            ?: return Response.NotFoundNamedHome
        return Response.Success(playerHome)
    }

    sealed class Response {
        data class Success(val playerHome: PlayerHome) : Response()
        object DefaultHomeAlreadyExists : Response()
        object NamedHomeAlreadyExists : Response()
        object NotFoundDefaultHome : Response()
        object NotFoundNamedHome : Response()
    }
}