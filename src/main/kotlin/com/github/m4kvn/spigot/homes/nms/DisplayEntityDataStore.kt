package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeLocation
import java.util.*

class DisplayEntityDataStore {
    private val chunkMap = hashMapOf<ChunkIndex, Set<PlayerHomeIndex>>()
    private val displayMap = hashMapOf<PlayerHomeIndex, List<DisplayEntity>>()

    private val PlayerHome.asPlayerHomeIndex: PlayerHomeIndex
        get() = PlayerHomeIndex(
            playerUUID = owner.playerUUID,
            location = location,
            homeName = name,
        )
    private val PlayerHome.asChunkIndex: ChunkIndex
        get() = ChunkIndex(
            x = location.chunkX,
            z = location.chunkZ,
        )

    fun getDisplayEntities(playerHome: PlayerHome): List<DisplayEntity> {
        val homeIndex = playerHome.asPlayerHomeIndex
        return displayMap[homeIndex] ?: emptyList()
    }

    fun getDisplayEntitiesIn(
        chunkX: Int,
        chunkZ: Int,
    ): List<DisplayEntity> {
        val chunkIndex = ChunkIndex(chunkX, chunkZ)
        val homeIndexSet = chunkMap[chunkIndex] ?: return emptyList()
        return homeIndexSet
            .mapNotNull { displayMap[it] }
            .flatten()
    }

    fun addDisplayEntities(
        playerHome: PlayerHome,
        entities: List<DisplayEntity>,
    ) {
        val chunkIndex = playerHome.asChunkIndex
        val playerHomeIndex = playerHome.asPlayerHomeIndex
        val currentSet = chunkMap[chunkIndex] ?: setOf()
        chunkMap[chunkIndex] = currentSet + playerHomeIndex
        displayMap[playerHomeIndex] = entities
    }

    fun removeDisplayEntities(playerHome: PlayerHome) {
        val chunkIndex = playerHome.asChunkIndex
        val playerHomeIndex = playerHome.asPlayerHomeIndex
        val currentSet = chunkMap[chunkIndex] ?: return
        chunkMap[chunkIndex] = currentSet - playerHomeIndex
        displayMap.remove(playerHomeIndex)
    }

    private data class PlayerHomeIndex(
        val playerUUID: UUID,
        val location: PlayerHomeLocation,
        val homeName: String?,
    )

    private data class ChunkIndex(
        val x: Int,
        val z: Int,
    )
}