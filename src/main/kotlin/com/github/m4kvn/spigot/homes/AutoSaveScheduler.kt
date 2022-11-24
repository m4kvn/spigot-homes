package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.messenger.Messenger
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class AutoSaveScheduler(
    private val plugin: JavaPlugin,
    private val messenger: Messenger,
    private val bukkitWrapper: BukkitWrapper,
    private val playerHomeManager: PlayerHomeManager,
) {
    private var bukkitTask: BukkitTask? = null

    fun run() {
        cancel()
        val scheduler = bukkitWrapper.getScheduler()
        bukkitTask = scheduler.runTaskTimer(plugin, Runnable {
            playerHomeManager.save()
            messenger.sendConsoleMessage(MESSAGE)
        }, INTERVAL, INTERVAL)
    }

    fun cancel() {
        bukkitTask?.takeUnless { it.isCancelled }?.cancel()
        bukkitTask = null
    }

    companion object {
        private const val ONE_SECOND = 20L
        private const val ONE_MINUTE = ONE_SECOND * 60
        private const val INTERVAL = ONE_MINUTE * 5
        private val MESSAGE = "${ChatColor.YELLOW}Completed saving the player's home data.${ChatColor.RESET}"
    }
}