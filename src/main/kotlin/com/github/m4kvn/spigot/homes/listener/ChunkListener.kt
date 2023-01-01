package com.github.m4kvn.spigot.homes.listener

import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.messenger.sendConsoleMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChunkListener : Listener, KoinComponent {
    private val displayManager by inject<DisplayEntityManager>()
    private val homeManager by inject<PlayerHomeManager>()

    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        val homes = homeManager.getHomesIn(event.chunk)
        if (homes.isNotEmpty()) {
            sendConsoleMessage {
                "${event.eventName}(event.chunk=${event.chunk}, homes.size=${homes.size})"
            }
        }
        displayManager.spawnEntities(event.chunk, homes)
    }

    @EventHandler
    fun onChunkUnLoad(event: ChunkUnloadEvent) {
        displayManager.despawnEntities(event.chunk)
    }
}