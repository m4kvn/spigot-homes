package com.github.m4kvn.spigot.homes.listener

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerRespawnListener : Listener, KoinComponent {
    private val playerHomeManager by inject<PlayerHomeManager>()
    private val bukkitWrapper by inject<BukkitWrapper>()

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val defaultHome = playerHomeManager.getDefaultHome(event.player.uniqueId) ?: return
        event.respawnLocation = bukkitWrapper.getLocation(defaultHome)
    }
}