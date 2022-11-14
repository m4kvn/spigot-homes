package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.bukkit.ProductionBukkitWrapper
import com.github.m4kvn.spigot.homes.command.HomesCommendExecutor
import com.github.m4kvn.spigot.homes.listener.ChunkListener
import com.github.m4kvn.spigot.homes.nms.*
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.playerhome.local.PlayerHomeDataStore
import com.github.m4kvn.spigot.homes.playerhome.local.ProductionPlayerHomeDataStore
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
class Main : JavaPlugin(), KoinComponent {
    private val playerHomeDataStore by inject<PlayerHomeDataStore>()
    private val playerHomeManager by inject<PlayerHomeManager>()

    private val module = module {
        single<JavaPlugin> { this@Main }
        single<BukkitWrapper> { ProductionBukkitWrapper() }
        single<NmsWrapper> { ProductionNmsWrapper() }
        single<PlayerHomeDataStore> { ProductionPlayerHomeDataStore(get()) }
        single { PlayerHomeManager(get()) }
        single { DisplayEntityDataStore() }
        single { DisplayEntityManager(get(), get()) }
    }

    override fun onLoad() {
        startKoin { modules(module) }
    }

    override fun onEnable() {
        register(ChunkListener())
        register(HomesCommendExecutor())
        playerHomeDataStore.connectDatabase()
        playerHomeDataStore.createTables()
        playerHomeManager.load()
    }

    override fun onDisable() {
        playerHomeManager.save()
        playerHomeDataStore.disconnectDatabase()
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