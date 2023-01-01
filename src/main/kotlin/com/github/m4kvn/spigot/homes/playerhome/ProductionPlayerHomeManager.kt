package com.github.m4kvn.spigot.homes.playerhome

import com.github.m4kvn.spigot.homes.asPlayerHomeChunk
import com.github.m4kvn.spigot.homes.messenger.appendMessage
import com.github.m4kvn.spigot.homes.messenger.sendConsole
import com.github.m4kvn.spigot.homes.playerhome.local.PlayerHomeDataStore
import org.bukkit.ChatColor
import org.bukkit.Chunk
import java.util.*

class ProductionPlayerHomeManager(
    private val dataStore: PlayerHomeDataStore,
) : PlayerHomeManager {
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

    override fun save(): List<PlayerHome> {
        sendConsole {
            appendMessage(ChatColor.DARK_AQUA) { SAVE_STARTED }
        }
        dataStore.storeOwnerList(allOwners)
        dataStore.storeDefaultHomeList(defaultHomeList)
        dataStore.storeNamedHomeList(namedHomeList)
        sendConsole {
            appendMessage(ChatColor.YELLOW) { SAVE_COMPLETED }
        }
        return allPlayerHome
    }

    override fun load(): List<PlayerHome> {
        sendConsole {
            appendMessage(ChatColor.DARK_AQUA) { LOAD_STARTED }
        }
        val defaultHomeList = dataStore.restoreDefaultHomeList()
        val defaultMap = defaultHomeList.associateBy({ it.owner.playerUUID }) { it }
        defaultHomeMap.clear()
        defaultHomeMap.putAll(defaultMap)
        val owners = dataStore.restoreOwnerList()
        val namedMap = owners.associateBy({ it.playerUUID }) { owner ->
            val ownerUUID = owner.playerUUID.toString()
            val namedHomeList = dataStore.restoreNamedHomeList(ownerUUID)
            HashMap(namedHomeList.associateBy({ it.name }) { it })
        }
        namedHomeMap.clear()
        namedHomeMap.putAll(namedMap)
        sendConsole {
            appendMessage(ChatColor.YELLOW) { LOAD_COMPLETED }
            appendMessage(ChatColor.WHITE) { " (size=${allPlayerHome.size})" }
        }
        return allPlayerHome
    }

    override fun addDefaultHome(playerHome: PlayerHome.Default): PlayerHomeManager.Response {
        val current = defaultHomeMap[playerHome.owner.playerUUID]
        if (current != null) {
            return PlayerHomeManager.Response.DefaultHomeAlreadyExists(current)
        }
        ownerMap[playerHome.owner.playerUUID] = playerHome.owner
        defaultHomeMap[playerHome.owner.playerUUID] = playerHome
        return PlayerHomeManager.Response.Success(playerHome)
    }

    override fun addNamedHome(playerHome: PlayerHome.Named): PlayerHomeManager.Response {
        val namedHomes = namedHomeMap.getOrPut(playerHome.owner.playerUUID) { hashMapOf() }
        val current = namedHomes[playerHome.name]
        if (current != null) {
            return PlayerHomeManager.Response.NamedHomeAlreadyExists(current)
        }
        ownerMap[playerHome.owner.playerUUID] = playerHome.owner
        namedHomes[playerHome.name] = playerHome
        return PlayerHomeManager.Response.Success(playerHome)
    }

    override fun getDefaultHome(ownerUUID: UUID): PlayerHome.Default? {
        return defaultHomeMap[ownerUUID]
    }

    override fun getNamedHome(ownerUUID: UUID, homeName: String): PlayerHome.Named? {
        return namedHomeMap[ownerUUID]?.get(homeName)
    }

    override fun getPlayerHomeListData(ownerUUID: UUID): PlayerHomeListData {
        return PlayerHomeListData(
            default = getDefaultHome(ownerUUID),
            namedList = namedHomeMap[ownerUUID]?.values?.toList() ?: emptyList(),
        )
    }

    override fun removeDefaultHome(ownerUUID: UUID): PlayerHomeManager.Response {
        val playerHome = defaultHomeMap.remove(ownerUUID)
            ?: return PlayerHomeManager.Response.NotFoundDefaultHome
        return PlayerHomeManager.Response.Success(playerHome)
    }

    override fun removeNamedHome(ownerUUID: UUID, homeName: String): PlayerHomeManager.Response {
        val playerHome = namedHomeMap[ownerUUID]?.remove(homeName)
            ?: return PlayerHomeManager.Response.NotFoundNamedHome
        return PlayerHomeManager.Response.Success(playerHome)
    }

    override fun replaceDefaultHome(playerHome: PlayerHome.Default) {
        defaultHomeMap[playerHome.owner.playerUUID] = playerHome
    }

    override fun replaceNamedHome(playerHome: PlayerHome.Named) {
        val namedHomes = namedHomeMap.getOrPut(playerHome.owner.playerUUID) { hashMapOf() }
        namedHomes[playerHome.name] = playerHome
    }

    override fun getHomesIn(chunk: Chunk): List<PlayerHome> {
        val homeChunk = chunk.asPlayerHomeChunk
        return allPlayerHome.filter { home ->
            home.location.chunk == homeChunk
        }
    }

    override fun getAllHomeList(): List<PlayerHome> {
        return allPlayerHome
    }
}