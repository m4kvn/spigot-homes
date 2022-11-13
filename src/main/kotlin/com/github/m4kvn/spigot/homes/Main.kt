package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.nms.*
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
    }

    override fun onEnable() {
        startKoin { modules(module) }
        val homesCommendExecutor = HomesCommendExecutor()
        val homesCommand = getCommand("homes")
        homesCommand?.setExecutor(homesCommendExecutor)
    }
}