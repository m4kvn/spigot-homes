package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.model.PlayerHome
import com.github.m4kvn.spigot.homes.model.PlayerHomeLocation
import java.util.*

class DisplayEntityDataStore {
    private val chunkMap = hashMapOf<ChunkIndex, Set<PlayerHomeIndex>>()
    private val displayMap = hashMapOf<PlayerHomeIndex, List<DisplayEntity>>()

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
        val chunkIndex = ChunkIndex(
            x = playerHome.location.chunkX,
            z = playerHome.location.chunkZ,
        )
        val playerHomeIndex = PlayerHomeIndex(
            playerUUID = playerHome.owner.playerUUID,
            location = playerHome.location,
            homeName = playerHome.name,
        )
        val currentSet = chunkMap[chunkIndex] ?: setOf()
        chunkMap[chunkIndex] = currentSet + playerHomeIndex
        displayMap[playerHomeIndex] = entities
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