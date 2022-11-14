package com.github.m4kvn.spigot.homes.playerhome

import com.github.m4kvn.spigot.homes.playerhome.local.PlayerHomeDataStore
import java.util.*

class PlayerHomeManager(
    private val dataStore: PlayerHomeDataStore,
) {
    private val ownerMap = hashMapOf<UUID, PlayerHomeOwner>()
    private val defaultHomeMap = hashMapOf<UUID, PlayerHome.Default>()
    private val namedHomeMap = hashMapOf<UUID, HashMap<String, PlayerHome.Named>>()

    private val defaultHomeList: List<PlayerHome.Default>
        get() = defaultHomeMap.values.toList()

    private val namedHomeList: List<PlayerHome.Named>
        get() = namedHomeMap.values.flatMap { it.values }

    private val allPlayerHome: List<PlayerHome>
        get() = defaultHomeList + namedHomeList

    private val allOwners: List<PlayerHomeOwner>
        get() = allPlayerHome
            .map { it.owner }
            .distinctBy { it.playerUUID }

    fun save() {
        dataStore.storeOwnerList(allOwners)
        dataStore.storeDefaultHomeList(defaultHomeList)
        dataStore.storeNamedHomeList(namedHomeList)
    }

    fun load() {
        val defaultHomeList = dataStore.restoreDefaultHomeList()
        val defaultMap = defaultHomeList.associateBy({ it.owner.playerUUID }) { it }
        defaultHomeMap.putAll(defaultMap)
        val owners = dataStore.restoreOwnerList()
        val namedMap = owners.associateBy({ it.playerUUID }) { owner ->
            val ownerUUID = owner.playerUUID.toString()
            val namedHomeList = dataStore.restoreNamedHomeList(ownerUUID)
            HashMap(namedHomeList.associateBy({ it.name }) { it })
        }
        namedHomeMap.putAll(namedMap)
    }

    fun addDefaultHome(playerHome: PlayerHome.Default): Response {
        val current = defaultHomeMap[playerHome.owner.playerUUID]
        if (current != null) {
            return Response.DefaultHomeAlreadyExists(current)
        }
        ownerMap[playerHome.owner.playerUUID] = playerHome.owner
        defaultHomeMap[playerHome.owner.playerUUID] = playerHome
        return Response.Success(playerHome)
    }

    fun addNamedHome(playerHome: PlayerHome.Named): Response {
        val namedHomes = namedHomeMap.getOrPut(playerHome.owner.playerUUID) { hashMapOf() }
        val current = namedHomes[playerHome.name]
        if (current != null) {
            return Response.NamedHomeAlreadyExists(current)
        }
        ownerMap[playerHome.owner.playerUUID] = playerHome.owner
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

    fun replaceDefaultHome(playerHome: PlayerHome.Default) {
        defaultHomeMap[playerHome.owner.playerUUID] = playerHome
    }

    fun replaceNamedHome(playerHome: PlayerHome.Named) {
        val namedHomes = namedHomeMap.getOrPut(playerHome.owner.playerUUID) { hashMapOf() }
        namedHomes[playerHome.name] = playerHome
    }

    sealed class Response {

        data class Success(
            val playerHome: PlayerHome,
        ) : Response()

        data class DefaultHomeAlreadyExists(
            val currentPlayerHome: PlayerHome.Default,
        ) : Response()

        data class NamedHomeAlreadyExists(
            val currentPlayerHome: PlayerHome.Named,
        ) : Response()

        object NotFoundDefaultHome : Response()
        object NotFoundNamedHome : Response()
    }
}