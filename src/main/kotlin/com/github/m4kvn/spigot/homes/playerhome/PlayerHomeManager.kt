package com.github.m4kvn.spigot.homes.playerhome

import org.bukkit.Chunk
import java.util.*

interface PlayerHomeManager {
    fun save(): List<PlayerHome>
    fun load(): List<PlayerHome>
    fun addDefaultHome(playerHome: PlayerHome.Default): Response
    fun addNamedHome(playerHome: PlayerHome.Named): Response
    fun getDefaultHome(ownerUUID: UUID): PlayerHome.Default?
    fun getNamedHome(ownerUUID: UUID, homeName: String): PlayerHome.Named?
    fun getPlayerHomeListData(ownerUUID: UUID): PlayerHomeListData
    fun removeDefaultHome(ownerUUID: UUID): Response
    fun removeNamedHome(ownerUUID: UUID, homeName: String): Response
    fun replaceDefaultHome(playerHome: PlayerHome.Default)
    fun replaceNamedHome(playerHome: PlayerHome.Named)
    fun getHomesIn(chunk: Chunk): List<PlayerHome>
    fun getAllHomeList(): List<PlayerHome>

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