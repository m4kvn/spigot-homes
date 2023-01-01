package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.MockBukkitWrapper
import com.github.m4kvn.spigot.homes.MockNmsWrapper
import com.github.m4kvn.spigot.homes.MockWorld
import com.github.m4kvn.spigot.homes.asPlayerHomeChunk
import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import org.bukkit.Chunk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("NonAsciiCharacters", "TestFunctionName")
class ProductionDisplayEntityManagerTest : KoinTest {
    private val displayEntityManager by inject<DisplayEntityManager>()
    private val displayEntityDataStore by inject<DisplayEntityDataStore>()
    private val world by inject<MockWorld>()

    private val testModule = module {
        val mockBukkitWrapper = MockBukkitWrapper()
        single<BukkitWrapper> { mockBukkitWrapper }
        single { mockBukkitWrapper.newMockWorld() }
        single<NmsWrapper> { MockNmsWrapper() }
        single<DisplayEntityDataStore> { ProductionDisplayEntityDataStore() }
        single<DisplayEntityManager> { ProductionDisplayEntityManager(get(), get(), get()) }
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
    fun 指定した全てのホームのDisplayEntityをスポーンさせることができる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val homes = createRandomNamedHomeList(chunk)

        displayEntityManager.spawnEntities(chunk, homes)

        val homeChunk = chunk.asPlayerHomeChunk
        val entities = displayEntityDataStore.getDisplayEntitiesIn(homeChunk)
        assertEquals(
            expected = 300,
            actual = entities.size,
        )
        assertTrue {
            entities.all { it.isAlive }
        }
    }

    @Test
    fun 指定したホームのDisplayEntityをスポーンさせる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val home = world.newRandomPlayerHomeDefault(chunk = chunk)

        displayEntityManager.spawnEntities(world, home)

        val entities = displayEntityDataStore.getDisplayEntities(home)
        assertEquals(
            expected = 2,
            actual = entities.size
        )
        assertTrue {
            entities.all { it.isAlive }
        }
    }

    @Test
    fun 指定したチャンクに対応する全てのDisplayEntityを消すことができる() {
        val chunk = world.newMockChunk(isLoaded = false)
        val home = world.newRandomPlayerHomeDefault(chunk = chunk)
        val entities = displayEntityManager.createEntities(world, home)
        displayEntityDataStore.addDisplayEntities(home, entities)

        displayEntityManager.despawnEntities(chunk)

        val homeChunk = chunk.asPlayerHomeChunk
        assertEquals(
            expected = emptyList(),
            actual = displayEntityDataStore.getDisplayEntitiesIn(homeChunk)
        )
    }

    @Test
    fun 全てのホームのDisplayEntityを消すことができる() {
        val chunk = world.newMockChunk(isLoaded = false)
        val entitiesMap = createRandomNamedHomeList(chunk).associateWith {
            displayEntityManager.createEntities(world, it)
        }

        val entities = entitiesMap.values.flatten()
        entitiesMap.forEach { (home, entities) ->
            displayEntityDataStore.addDisplayEntities(home, entities)
        }
        assertEquals(
            expected = entities.size,
            actual = displayEntityDataStore.getDisplayEntities().size,
        )
        assertTrue {
            displayEntityDataStore.getDisplayEntities()
                .all { entities.contains(it) }
        }

        displayEntityManager.despawnAllEntities()

        assertEquals(
            expected = emptyList(),
            actual = displayEntityDataStore.getDisplayEntities(),
        )
    }

    @Test
    fun 指定したホームのDisplayEntityを消す() {
        val home = world.newRandomPlayerHomeDefault()
        val entities = displayEntityManager.createEntities(world, home)
        displayEntityDataStore.addDisplayEntities(home, entities)
        assertEquals(
            expected = entities,
            actual = displayEntityDataStore.getDisplayEntities()
        )

        displayEntityManager.despawnEntities(home)

        assertEquals(
            expected = emptyList(),
            actual = displayEntityDataStore.getDisplayEntities()
        )
    }

    @Test
    fun 指定したデフォルトホームのDisplayEntityを生成することができる() {
        val defaultHome = world.newRandomPlayerHomeDefault()

        val entities = displayEntityManager.createEntities(world, defaultHome)

        assertEquals(
            expected = 2,
            actual = entities.size,
        )
        assertEquals(
            expected = "${defaultHome.owner.playerName}'s",
            actual = entities[0].customText,
        )
        assertEquals(
            expected = "default home",
            actual = entities[1].customText,
        )
    }

    @Test
    fun 指定した名前付きホームのDisplayEntityを生成することができる() {
        val namedHome = world.newRandomPlayerHomeNamed()

        val entities = displayEntityManager.createEntities(world, namedHome)

        assertEquals(
            expected = 3,
            actual = entities.size,
        )
        assertEquals(
            expected = "${namedHome.owner.playerName}'s",
            actual = entities[0].customText,
        )
        assertEquals(
            expected = "home named",
            actual = entities[1].customText,
        )
        assertEquals(
            expected = "<${namedHome.name}>",
            actual = entities[2].customText,
        )
    }

    private fun createRandomNamedHomeList(chunk: Chunk): List<PlayerHome.Named> {
        return List(100) { world.newRandomPlayerHomeNamed(chunk = chunk) }
    }
}