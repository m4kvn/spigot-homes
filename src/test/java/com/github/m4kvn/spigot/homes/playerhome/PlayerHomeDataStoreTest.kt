package com.github.m4kvn.spigot.homes.playerhome

import com.github.m4kvn.spigot.homes.MockPlayerHomeDataStore
import com.github.m4kvn.spigot.homes.MockWorld
import com.github.m4kvn.spigot.homes.messenger.Messenger
import com.github.m4kvn.spigot.homes.playerhome.local.PlayerHomeDataStore
import com.github.m4kvn.spigot.homes.playerhome.local.ProductionPlayerHomeDataStore
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

@Suppress("NonAsciiCharacters", "TestFunctionName")
class PlayerHomeDataStoreTest : KoinTest {
    private val dataStore by inject<PlayerHomeDataStore>()
    private val world by inject<MockWorld>()

    private val testModule = module {
        single { MockWorld() }
        single<JavaPlugin> { mock() }
        single<Messenger> { mock() }
        single { PlayerHomeManager(get(), get()) }
        single { ProductionPlayerHomeDataStore(get()) }
        single<PlayerHomeDataStore> { MockPlayerHomeDataStore(get()) }
    }

    @BeforeEach
    fun setup() {
        startKoin { modules(testModule) }
        dataStore.connectDatabase()
        dataStore.createTables()
    }

    @AfterEach
    fun teardown() {
        dataStore.disconnectDatabase()
        stopKoin()
    }

    @Test
    fun デフォルトのホームを正常に保存し取得することができる() {
        val defaultHomeList = listOf(
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
        )
        assertDoesNotThrow {
            dataStore.storeDefaultHomeList(defaultHomeList)
        }
        assertEquals(
            expected = defaultHomeList,
            actual = dataStore.restoreDefaultHomeList(),
        )
    }

    @Test
    fun 名前付きのホームを正常に保存することができる() {
        val namedHomeList = listOf(
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
        )
        assertDoesNotThrow {
            dataStore.storeNamedHomeList(namedHomeList)
        }
    }

    @Test
    fun 指定したユーザーの名前付きホームを正しく取得できる() {
        val owner = world.newRandomPlayerHomeOwner()
        val ownerUUID = owner.playerUUID.toString()
        val sameOwnerNamedHomeList = listOf(
            world.newRandomPlayerHomeNamed(owner = owner),
            world.newRandomPlayerHomeNamed(owner = owner),
            world.newRandomPlayerHomeNamed(owner = owner),
        )
        val namedHomeList = sameOwnerNamedHomeList + listOf(
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
        )
        dataStore.storeNamedHomeList(namedHomeList)
        assertEquals(
            expected = sameOwnerNamedHomeList,
            actual = dataStore.restoreNamedHomeList(ownerUUID),
        )
    }
}