package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeChunk

class ProductionDisplayEntityDataStore : DisplayEntityDataStore {
    private val chunkMap = hashMapOf<PlayerHomeChunk, Set<PlayerHome>>()
    private val displayMap = hashMapOf<PlayerHome, List<DisplayEntity>>()

    override fun getDisplayEntities(): List<DisplayEntity> {
        return displayMap.values.flatten()
    }

    override fun getDisplayEntities(home: PlayerHome): List<DisplayEntity> {
        return displayMap[home] ?: emptyList()
    }

    override fun getDisplayEntitiesIn(chunk: PlayerHomeChunk): List<DisplayEntity> {
        val homeSet = chunkMap[chunk] ?: return emptyList()
        return homeSet
            .mapNotNull { displayMap[it] }
            .flatten()
    }

    override fun addDisplayEntities(home: PlayerHome, entities: List<DisplayEntity>) {
        val currentSet = chunkMap[home.location.chunk] ?: setOf()
        chunkMap[home.location.chunk] = currentSet + home
        displayMap[home] = entities
    }

    override fun removeDisplayEntitiesIn(chunk: PlayerHomeChunk): List<DisplayEntity> {
        val homeSet = chunkMap.remove(chunk) ?: return emptyList()
        return homeSet
            .mapNotNull { displayMap.remove(it) }
            .flatten()
    }

    override fun removeDisplayEntities(home: PlayerHome): List<DisplayEntity> {
        val entities = displayMap.remove(home) ?: return emptyList()
        val currentSet = chunkMap[home.location.chunk]
        if (currentSet != null) {
            chunkMap[home.location.chunk] = currentSet - home
        }
        return entities
    }

    override fun removeAllDisplayEntities(): List<DisplayEntity> {
        val entities = getDisplayEntities()
        chunkMap.clear()
        displayMap.clear()
        return entities
    }
}