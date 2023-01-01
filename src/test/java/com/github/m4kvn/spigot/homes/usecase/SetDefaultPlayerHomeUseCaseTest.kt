package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.MockWorld
import com.github.m4kvn.spigot.homes.nms.DisplayEntityDataStore
import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
import com.github.m4kvn.spigot.homes.playerhome.local.PlayerHomeDataStore
import com.github.m4kvn.spigot.homes.testModule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@Suppress("NonAsciiCharacters", "TestFunctionName")
internal class SetDefaultPlayerHomeUseCaseTest : KoinTest {
    private val world by inject<MockWorld>()
    private val homeManager by inject<PlayerHomeManager>()
    private val homeDataStore by inject<PlayerHomeDataStore>()
    private val displayStore by inject<DisplayEntityDataStore>()
    private val displayManager by inject<DisplayEntityManager>()
    private val setDefaultPlayerHomeUseCase by inject<SetDefaultPlayerHomeUseCase>()

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(testModule, module {
                single { CreateDefaultPlayerHomeUseCase() }
                single { SetDefaultPlayerHomeUseCase(get(), get(), get()) }
            })
        }
        homeDataStore.connectDatabase()
        homeDataStore.createTables()
    }

    @AfterEach
    fun tearDown() {
        homeDataStore.disconnectDatabase()
        stopKoin()
    }

    @Test
    fun 新しいデフォルトホームを設定できる() {
        val player = world.newMockPlayer()
        setDefaultPlayerHomeUseCase(player)
        assertNotEquals(
            illegal = null,
            actual = homeManager.getDefaultHome(player.uniqueId),
        )
    }

    @Test
    fun 新しいデフォルトホームを設定した場合に既存のデフォルトホームを削除する() {
        val player = world.newMockPlayer()
        val playerHome = world.newRandomPlayerHomeDefault(player = player)
        homeManager.addDefaultHome(playerHome)
        setDefaultPlayerHomeUseCase(player)
        assertNotEquals(
            illegal = playerHome,
            actual = homeManager.getDefaultHome(player.uniqueId)
        )
    }

    @Test
    fun 新しいデフォルトホームを設定したときDisplayEntityを追加する() {
        val chunk = world.newMockChunk(isLoaded = true)
        val location = world.newMockLocation(locationChunk = chunk)
        val player = world.newMockPlayer(playerLocation = location)
        val newDefaultHome = setDefaultPlayerHomeUseCase(player)
        val entities = displayStore.getDisplayEntities(newDefaultHome)
        assertTrue { entities.isNotEmpty() }
        assertTrue { entities.all { it.isAlive } }
    }

    @Test
    fun 新しいデフォルトホームを設定したときに既存のデフォルトホームのDisplayEntityを削除する() {
        val player = world.newMockPlayer()
        val home = world.newRandomPlayerHomeDefault(player = player)
        val entities = displayManager.createEntities(world, home)

        homeManager.addDefaultHome(home)
        assertEquals(
            expected = home,
            actual = homeManager.getDefaultHome(player.uniqueId)
        )

        displayStore.addDisplayEntities(home, entities)
        assertEquals(
            expected = entities,
            actual = displayStore.getDisplayEntities(home)
        )

        setDefaultPlayerHomeUseCase(player)

        assertEquals(
            expected = emptyList(),
            actual = displayStore.getDisplayEntities(home)
        )
    }
}