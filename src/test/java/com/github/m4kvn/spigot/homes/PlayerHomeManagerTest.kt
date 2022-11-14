package com.github.m4kvn.spigot.homes

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.*
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("NonAsciiCharacters", "TestFunctionName")
class PlayerHomeManagerTest : KoinTest {
    private val manager by inject<PlayerHomeManager>()
    private val world by inject<MockWorld>()

    private val testModule = module {
        single { MockWorld() }
        single { PlayerHomeManager() }
    }

    @BeforeEach
    fun setup() {
        startKoin { modules(testModule) }
    }

    @AfterEach
    fun teardown() {
        stopKoin()
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
}