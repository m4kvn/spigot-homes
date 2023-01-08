package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.bukkit.ProductionBukkitWrapper
import com.github.m4kvn.spigot.homes.command.HomesCommandExecutor
import com.github.m4kvn.spigot.homes.listener.ChunkListener
import com.github.m4kvn.spigot.homes.listener.PlayerRespawnListener
import com.github.m4kvn.spigot.homes.messenger.Messenger
import com.github.m4kvn.spigot.homes.messenger.ProductionMessenger
import com.github.m4kvn.spigot.homes.nms.*
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.playerhome.ProductionPlayerHomeManager
import com.github.m4kvn.spigot.homes.playerhome.TemporaryPlayerHomeManager
import com.github.m4kvn.spigot.homes.playerhome.local.PlayerHomeDataStore
import com.github.m4kvn.spigot.homes.playerhome.local.ProductionPlayerHomeDataStore
import com.github.m4kvn.spigot.homes.usecase.*
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
        single<PlayerHomeManager> { ProductionPlayerHomeManager(get()) }
        single<Messenger> { ProductionMessenger(get()) }
        single { TemporaryPlayerHomeManager() }
        single<DisplayEntityDataStore> { ProductionDisplayEntityDataStore() }
        single<DisplayEntityManager> { ProductionDisplayEntityManager(get(), get(), get()) }
        single { AutoSaveScheduler(get(), get(), get(), get()) }
    }

    private val useCaseModule = module {
        factory { CreateDefaultPlayerHomeUseCase() }
        factory { CreateNamedPlayerHomeUseCase() }
        factory { CreateTemporaryPlayerHomeUseCase() }
        factory { SetDefaultPlayerHomeUseCase(get(), get(), get()) }
        factory { SetNamedPlayerHomeUseCase(get(), get(), get()) }
        factory { RemoveDefaultHomeUseCase(get(), get()) }
        factory { RemoveNamedHomeUseCase(get(), get()) }
        factory { TeleportPlayerHomeUseCase(get(), get(), get()) }
        factory { GetTemporaryPlayerHomeUseCase(get()) }
        factory { SaveTemporaryPlayerHomeUseCase(get()) }
    }

    override fun onLoad() {
        startKoin { modules(module, useCaseModule) }
        messenger.sendConsoleMessage("onLoaded")
    }

    override fun onEnable() {
        register(ChunkListener())
        register(PlayerRespawnListener())
        HomesCommandExecutor().register()
        playerHomeDataStore.connectDatabase()
        playerHomeDataStore.createTables()
        playerHomeManager.load()
        autoSaveScheduler.run()
        messenger.sendConsoleMessage("onEnabled")
    }

    override fun onDisable() {
        autoSaveScheduler.cancel()
        playerHomeManager.save()
        displayEntityManager.despawnAllEntities()
        playerHomeDataStore.disconnectDatabase()
        messenger.sendConsoleMessage("onDisabled")
    }

    private fun register(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }
}