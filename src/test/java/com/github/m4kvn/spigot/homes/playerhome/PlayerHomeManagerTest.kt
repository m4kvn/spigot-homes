package com.github.m4kvn.spigot.homes.playerhome

import com.github.m4kvn.spigot.homes.MockPlayerHomeDataStore
import com.github.m4kvn.spigot.homes.MockWorld
import com.github.m4kvn.spigot.homes.playerhome.local.PlayerHomeDataStore
import com.github.m4kvn.spigot.homes.playerhome.local.ProductionPlayerHomeDataStore
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.kotlin.mock
import java.util.*
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("NonAsciiCharacters", "TestFunctionName")
class PlayerHomeManagerTest : KoinTest {
    private val dataStore by inject<PlayerHomeDataStore>()
    private val manager by inject<PlayerHomeManager>()
    private val world by inject<MockWorld>()

    private val testModule = module {
        single { MockWorld() }
        single { mock<JavaPlugin>() }
        single { PlayerHomeManager(get()) }
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
    fun 追加されているデフォルトホームを正しく保存できる() {
        val defaultHomeList = listOf(
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
        )
        defaultHomeList.forEach {
            manager.addDefaultHome(it)
        }
        manager.save()
        assertTrue {
            dataStore.restoreDefaultHomeList().count() == defaultHomeList.count()
        }
        assertTrue {
            dataStore.restoreDefaultHomeList().all { defaultHomeList.contains(it) }
        }
    }

    @Test
    fun 保存されているデフォルトホームを正しく読み込める() {
        val defaultHomeList = listOf(
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
            world.newRandomPlayerHomeDefault(),
        )
        dataStore.storeDefaultHomeList(defaultHomeList)
        assertTrue {
            manager.load()
                .filterIsInstance<PlayerHome.Default>()
                .all { defaultHomeList.contains(it) }
        }
    }

    @Test
    fun 追加した名前付きホームを正しく保存できる() {
        val namedHomeList = listOf(
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
        )
        namedHomeList.forEach {
            manager.addNamedHome(it)
        }
        manager.save()
        assertTrue {
            namedHomeList.all { namedHome ->
                val ownerUUID = namedHome.owner.playerUUID.toString()
                val sameOwnerCount = namedHomeList.count { it.owner == namedHome.owner }
                dataStore.restoreNamedHomeList(ownerUUID).count() == sameOwnerCount
            }
        }
        assertTrue {
            namedHomeList.all {
                val ownerUUID = it.owner.playerUUID.toString()
                dataStore.restoreNamedHomeList(ownerUUID).first() == it
            }
        }
    }

    @Test
    fun 同じユーザーが追加した名前付きホームを正しく保存できる() {
        val owner = world.newRandomPlayerHomeOwner()
        val ownerUUID = owner.playerUUID.toString()
        val sameOwnerNamedHomeList = listOf(
            world.newRandomPlayerHomeNamed(owner = owner),
            world.newRandomPlayerHomeNamed(owner = owner),
            world.newRandomPlayerHomeNamed(owner = owner),
            world.newRandomPlayerHomeNamed(owner = owner),
            world.newRandomPlayerHomeNamed(owner = owner),
        )
        val namedHomeList = sameOwnerNamedHomeList + listOf(
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
        )
        namedHomeList.forEach {
            manager.addNamedHome(it)
        }
        manager.save()
        assertTrue {
            dataStore.restoreNamedHomeList(ownerUUID).count() == sameOwnerNamedHomeList.count()
        }
        assertTrue {
            dataStore.restoreNamedHomeList(ownerUUID).all {
                sameOwnerNamedHomeList.contains(it)
            }
        }
    }

    @Test
    fun 保存されている名前付きホームを正しく読み込める() {
        val owner = world.newRandomPlayerHomeOwner()
        val sameOwnerNamedHomeList = listOf(
            world.newRandomPlayerHomeNamed(owner = owner),
            world.newRandomPlayerHomeNamed(owner = owner),
            world.newRandomPlayerHomeNamed(owner = owner),
            world.newRandomPlayerHomeNamed(owner = owner),
            world.newRandomPlayerHomeNamed(owner = owner),
        )
        val namedHomeList = sameOwnerNamedHomeList + listOf(
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
            world.newRandomPlayerHomeNamed(),
        )
        val ownerList = namedHomeList
            .map { it.owner }
            .distinctBy { it.playerUUID }
        dataStore.storeOwnerList(ownerList)
        dataStore.storeNamedHomeList(namedHomeList)
        assertTrue {
            manager.load()
                .filterIsInstance<PlayerHome.Named>()
                .all { namedHomeList.contains(it) }
        }
    }

    @Test
    fun 追加したデフォルトホームを正しく取得できる() {
        val playerHome = world.newRandomPlayerHomeDefault()
        val response = manager.addDefaultHome(playerHome)
        assertTrue {
            response is PlayerHomeManager.Response.Success
        }
        val actual = manager.getDefaultHome(
            playerHome.owner.playerUUID
        )
        assertEquals(playerHome, actual)
    }

    @Test
    fun 追加した名前付きホームを正しく取得できる() {
        val playerHome = world.newRandomPlayerHomeNamed()
        val response = manager.addNamedHome(playerHome)
        assertTrue {
            response is PlayerHomeManager.Response.Success
        }
        val actual = manager.getNamedHome(
            ownerUUID = playerHome.owner.playerUUID,
            homeName = playerHome.name,
        )
        assertEquals(playerHome, actual)
    }

    @Test
    fun デフォルトホームが既にあった場合に新しいものを追加できない() {
        val playerHome1 = world.newRandomPlayerHomeDefault()
        val playerHome2 = world.newRandomPlayerHomeDefault(
            owner = playerHome1.owner,
        )
        manager.addDefaultHome(playerHome1)
        val response = manager.addDefaultHome(playerHome2)
        assertTrue {
            response is PlayerHomeManager.Response.DefaultHomeAlreadyExists
        }
        val actual = manager.getDefaultHome(playerHome2.owner.playerUUID)
        assertEquals(playerHome1, actual)
        assertNotEquals(playerHome2, actual)
    }

    @Test
    fun 同じ名前の名前付きホームが既にあった場合に新しいものを追加できない() {
        val playerHome1 = world.newRandomPlayerHomeNamed()
        val playerHome2 = world.newRandomPlayerHomeNamed(
            owner = playerHome1.owner,
            homeName = playerHome1.name,
        )
        manager.addNamedHome(playerHome1)
        val response = manager.addNamedHome(playerHome2)
        assertTrue {
            response is PlayerHomeManager.Response.NamedHomeAlreadyExists
        }
        val actual = manager.getNamedHome(
            playerHome2.owner.playerUUID,
            playerHome2.name,
        )
        assertEquals(playerHome1, actual)
        assertNotEquals(playerHome2, actual)
    }

    @Test
    fun 削除したデフォルトホームを取得できない() {
        val playerHome = world.newRandomPlayerHomeDefault()
        manager.addDefaultHome(playerHome)
        val response = manager.removeDefaultHome(
            playerHome.owner.playerUUID,
        ) as PlayerHomeManager.Response.Success
        assertEquals(
            expected = playerHome,
            actual = response.playerHome,
        )
        assertNull(
            actual = manager.getDefaultHome(
                playerHome.owner.playerUUID,
            )
        )
    }

    @Test
    fun 削除した名前付きホームを取得できない() {
        val playerHome = world.newRandomPlayerHomeNamed()
        manager.addNamedHome(playerHome)
        val response = manager.removeNamedHome(
            playerHome.owner.playerUUID,
            playerHome.name,
        ) as PlayerHomeManager.Response.Success
        assertEquals(
            expected = playerHome,
            actual = response.playerHome,
        )
        assertNull(
            manager.getNamedHome(
                playerHome.owner.playerUUID,
                playerHome.name,
            )
        )
    }

    @Test
    fun 存在しないデフォルトホームの削除は失敗する() {
        val ownerUUID = UUID.randomUUID()
        assertEquals(
            expected = PlayerHomeManager.Response.NotFoundDefaultHome,
            actual = manager.removeDefaultHome(ownerUUID)
        )
    }

    @Test
    fun 存在しない名前付きホームの削除は失敗する() {
        val ownerUUID = UUID.randomUUID()
        val homeName = "${Random.nextInt()}"
        assertEquals(
            expected = PlayerHomeManager.Response.NotFoundNamedHome,
            actual = manager.removeNamedHome(ownerUUID, homeName)
        )
    }

    @Test
    fun デフォルトホームが既にあった場合でも新しいものを上書きできる() {
        val playerHome1 = world.newRandomPlayerHomeDefault()
        val playerHome2 = world.newRandomPlayerHomeDefault(
            owner = playerHome1.owner,
        )
        manager.addDefaultHome(playerHome1)
        manager.replaceDefaultHome(playerHome2)
        assertNotEquals(
            illegal = playerHome1,
            actual = manager.getDefaultHome(playerHome1.owner.playerUUID),
        )
        assertEquals(
            expected = playerHome2,
            actual = manager.getDefaultHome(playerHome1.owner.playerUUID),
        )
    }

    @Test
    fun 同じ名前の名前付きホームが既にあった場合でも新しいものを上書きできる() {
        val playerHome1 = world.newRandomPlayerHomeNamed()
        val playerHome2 = world.newRandomPlayerHomeNamed(
            owner = playerHome1.owner,
            homeName = playerHome1.name,
        )
        manager.addNamedHome(playerHome1)
        manager.replaceNamedHome(playerHome2)
        assertNotEquals(
            illegal = playerHome1,
            actual = manager.getNamedHome(
                ownerUUID = playerHome1.owner.playerUUID,
                homeName = playerHome1.name,
            )
        )
        assertEquals(
            expected = playerHome2,
            actual = manager.getNamedHome(
                ownerUUID = playerHome1.owner.playerUUID,
                homeName = playerHome1.name,
            )
        )
    }

    @Test
    fun 指定したプレイヤーのホームのリストを取得できる() {
        val player = world.newMockPlayer()
        val defaultHome = world.newRandomPlayerHomeDefault(player = player)
        manager.addDefaultHome(defaultHome)
        val namedHomeList = listOf(
            world.newRandomPlayerHomeNamed(player = player),
            world.newRandomPlayerHomeNamed(player = player),
            world.newRandomPlayerHomeNamed(player = player),
            world.newRandomPlayerHomeNamed(player = player),
        )
        namedHomeList.forEach {
            manager.addNamedHome(it)
        }
        val data = manager.getPlayerHomeListData(player.uniqueId)
        assertEquals(
            expected = defaultHome,
            actual = data.default,
        )
        assertTrue {
            data.namedList.all { namedHomeList.contains(it) }
        }
    }
}