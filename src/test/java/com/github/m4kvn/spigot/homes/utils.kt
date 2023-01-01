package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.messenger.Messenger
import com.github.m4kvn.spigot.homes.nms.*
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.playerhome.ProductionPlayerHomeManager
import com.github.m4kvn.spigot.homes.playerhome.local.PlayerHomeDataStore
import com.github.m4kvn.spigot.homes.playerhome.local.ProductionPlayerHomeDataStore
import org.bukkit.plugin.java.JavaPlugin
import org.koin.dsl.module
import org.mockito.kotlin.mock

val testModule = module {
    val mockBukkitWrapper = MockBukkitWrapper()
    single<JavaPlugin> { mock() }
    single<Messenger> { mock() }
    single<NmsWrapper> { MockNmsWrapper() }
    single<BukkitWrapper> { mockBukkitWrapper }
    single<DisplayEntityDataStore> { ProductionDisplayEntityDataStore() }
    single<DisplayEntityManager> { ProductionDisplayEntityManager(get(), get(), get()) }
    single<PlayerHomeManager> { ProductionPlayerHomeManager(get()) }
    single<PlayerHomeDataStore> { MockPlayerHomeDataStore(ProductionPlayerHomeDataStore(get())) }
    single { mockBukkitWrapper.newMockWorld() }
}