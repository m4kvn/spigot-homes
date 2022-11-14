package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.bukkit.ProductionBukkitWrapper
import com.github.m4kvn.spigot.homes.command.HomesCommendExecutor
import com.github.m4kvn.spigot.homes.listener.ChunkListener
import com.github.m4kvn.spigot.homes.nms.*
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
class Main : JavaPlugin() {

    private val module = module {
        single<JavaPlugin> { this@Main }
        single<BukkitWrapper> { ProductionBukkitWrapper() }
        single<NmsWrapper> { ProductionNmsWrapper() }
        single { DisplayEntityDataStore() }
        single { DisplayEntityManager(get(), get()) }
        single { PlayerHomeManager() }
    }

    override fun onEnable() {
        startKoin { modules(module) }
        register(ChunkListener())
        register(HomesCommendExecutor())
    }

    private fun register(executor: CommandExecutor) {
        getCommand(PLUGIN_COMMAND_NAME)?.setExecutor(executor)
    }

    private fun register(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    companion object {
        private const val PLUGIN_COMMAND_NAME = "homes"
    }
}