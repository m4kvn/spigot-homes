package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.bukkit.ProductionBukkitWrapper
import com.github.m4kvn.spigot.homes.command.HomesCommendExecutor
import com.github.m4kvn.spigot.homes.listener.ChunkListener
import com.github.m4kvn.spigot.homes.messenger.Messenger
import com.github.m4kvn.spigot.homes.messenger.ProductionMessenger
import com.github.m4kvn.spigot.homes.nms.*
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.playerhome.local.PlayerHomeDataStore
import com.github.m4kvn.spigot.homes.playerhome.local.ProductionPlayerHomeDataStore
import com.github.m4kvn.spigot.homes.usecase.*
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Suppress("unused")
class Main : JavaPlugin(), KoinComponent {
    private val displayEntityManager by inject<DisplayEntityManager>()
    private val playerHomeDataStore by inject<PlayerHomeDataStore>()
    private val playerHomeManager by inject<PlayerHomeManager>()
    private val autoSaveScheduler by inject<AutoSaveScheduler>()
    private val messenger by inject<Messenger>()

    private val module = module {
        single<JavaPlugin> { this@Main }
        single<BukkitWrapper> { ProductionBukkitWrapper() }
        single<NmsWrapper> { ProductionNmsWrapper() }
        single<PlayerHomeDataStore> { ProductionPlayerHomeDataStore(get()) }
        single<Messenger> { ProductionMessenger(get()) }
        single { PlayerHomeManager(get(), get()) }
        single { DisplayEntityDataStore() }
        single { DisplayEntityManager(get(), get(), get()) }
        single { AutoSaveScheduler(get(), get(), get(), get()) }
    }

    private val useCaseModule = module {
        factory { CreateDefaultPlayerHomeUseCase() }
        factory { CreateNamedPlayerHomeUseCase() }
        factory { SetDefaultPlayerHomeUseCase(get(), get(), get()) }
        factory { SetNamedPlayerHomeUseCase(get(), get(), get()) }
        factory { RemoveDefaultHomeUseCase(get(), get()) }
        factory { RemoveNamedHomeUseCase(get(), get()) }
    }

    override fun onLoad() {
        startKoin { modules(module, useCaseModule) }
        messenger.sendConsoleMessage("onLoaded")
    }

    override fun onEnable() {
        register(ChunkListener())
        register(HomesCommendExecutor())
        playerHomeDataStore.connectDatabase()
        playerHomeDataStore.createTables()
        val allPlayerHome = playerHomeManager.load()
        displayEntityManager.addEntities(allPlayerHome)
        autoSaveScheduler.run()
        messenger.sendConsoleMessage("onEnabled")
    }

    override fun onDisable() {
        autoSaveScheduler.cancel()
        val allPlayerHome = playerHomeManager.save()
        displayEntityManager.removeEntities(allPlayerHome)
        playerHomeDataStore.disconnectDatabase()
        messenger.sendConsoleMessage("onDisabled")
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