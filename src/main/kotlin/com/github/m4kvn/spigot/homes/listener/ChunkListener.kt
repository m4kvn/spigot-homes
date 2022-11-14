package com.github.m4kvn.spigot.homes.listener

import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChunkListener : Listener, KoinComponent {
    private val displayManager by inject<DisplayEntityManager>()

    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        displayManager.spawnEntitiesIn(event.chunk)
    }

    @EventHandler
    fun onChunkUnLoad(event: ChunkUnloadEvent) {
        displayManager.despawnEntitiesIn(event.chunk)
    }
}