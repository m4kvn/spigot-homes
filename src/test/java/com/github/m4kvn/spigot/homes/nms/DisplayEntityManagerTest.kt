package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.MockBukkitWrapper
import com.github.m4kvn.spigot.homes.MockNmsWrapper
import com.github.m4kvn.spigot.homes.MockWorld
import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
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
class DisplayEntityManagerTest : KoinTest {
    private val manager by inject<DisplayEntityManager>()
    private val dataStore by inject<DisplayEntityDataStore>()
    private val world by inject<MockWorld>()

    private val testModule = module {
        single<BukkitWrapper> { MockBukkitWrapper() }
        single<NmsWrapper> { MockNmsWrapper() }
        single { DisplayEntityDataStore() }
        single { DisplayEntityManager(get(), get(), get()) }
        single { MockWorld() }
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
    fun デフォルトのホームでDisplayEntityを正しく生成できる() {
        val playerHome = world.newRandomPlayerHomeDefault()
        val entities = manager.createEntities(world, playerHome)

        entities[0].also {
            val expected = "${playerHome.owner.playerName}'s"
            val actual = it.text
            assertEquals(expected, actual)
        }
        entities[1].also {
            val expected = "default home"
            val actual = it.text
            assertEquals(expected, actual)
        }
    }

    @Test
    fun 名前付きのホームでDisplayEntityが正しく生成できる() {
        val playerHome = world.newRandomPlayerHomeNamed()
        val entities = manager.createEntities(world, playerHome)

        entities[0].also {
            val expected = "${playerHome.owner.playerName}'s"
            val actual = it.text
            assertEquals(expected, actual)
        }
        entities[1].also {
            val expected = "home named"
            val actual = it.text
            assertEquals(expected, actual)
        }
        entities[2].also {
            val expected = "<${playerHome.name}>"
            val actual = it.text
            assertEquals(expected, actual)
        }
    }

    @Test
    fun ホームデータを正しく追加できる() {
        val playerHome = world.newRandomPlayerHomeNamed()
        val entities = manager.createEntities(world, playerHome)
        manager.addEntities(playerHome, entities)

        val actual = dataStore.getDisplayEntitiesIn(
            chunkX = playerHome.location.chunkX,
            chunkZ = playerHome.location.chunkZ,
        )
        assertEquals(entities, actual)
    }

    @Test
    fun 追加済みのホームデータを正しく削除できる() {
        val playerHome = world.newRandomPlayerHomeNamed()
        val entities = manager.createEntities(world, playerHome)
        manager.addEntities(playerHome, entities)
        assertEquals(
            expected = entities,
            actual = dataStore.getDisplayEntitiesIn(
                playerHome.location.chunkX,
                playerHome.location.chunkZ,
            )
        )
        manager.removeEntities(playerHome)
        assertEquals(
            expected = emptyList(),
            actual = dataStore.getDisplayEntitiesIn(
                playerHome.location.chunkX,
                playerHome.location.chunkZ,
            )
        )
    }

    @Test
    fun 読み込まれていないChunkのDisplayEntityがspawnされない() {
        val chunk = world.newMockChunk(isLoaded = false)
        val playerHome = world.newRandomPlayerHomeNamed()
        val entities = manager.createEntities(world, playerHome)
        manager.addEntities(playerHome, entities)
        manager.spawnEntitiesIn(chunk)
        assertTrue {
            entities.all { it.isDead }
        }
    }

    @Test
    fun 読み込まれているChunkのDisplayEntityがspawnされる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val playerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        val entities = manager.createEntities(world, playerHome)
        manager.addEntities(playerHome, entities)
        manager.spawnEntitiesIn(chunk)
        assertTrue {
            entities.none { it.isDead }
        }
    }

    @Test
    fun 指定されたChunk内にある追加済みのDisplayEntityをdespawnできる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val playerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        val entities = manager.createEntities(world, playerHome)
        manager.addEntities(playerHome, entities)
        manager.spawnEntitiesIn(chunk)
        assertTrue {
            entities.none { it.isDead }
        }
        manager.despawnEntitiesIn(chunk)
        assertTrue {
            entities.all { it.isDead }
        }
    }

    @Test
    fun 読み込まれていないChunkのDisplayEntityは追加されるがspawnされない() {
        val chunk = world.newMockChunk(isLoaded = false)
        val playerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        manager.spawnEntities(world, playerHome)
        val entities = dataStore.getDisplayEntitiesIn(chunk.x, chunk.z)
        assertTrue {
            entities.all { it.isDead }
        }
    }

    @Test
    fun 読み込まれているChunkのDisplayEntityが追加されspawnされる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val playerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        manager.spawnEntities(world, playerHome)
        val entities = dataStore.getDisplayEntitiesIn(chunk.x, chunk.z)
        assertTrue {
            entities.none { it.isDead }
        }
    }

    @Test
    fun 既にspawnしているDisplayEntityをdespawnしてから削除できる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val playerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        val entities = manager.createEntities(world, playerHome)
        manager.addEntities(playerHome, entities)
        manager.spawnEntitiesIn(chunk)
        assertTrue {
            entities.none { it.isDead }
        }
        assertEquals(
            expected = entities,
            actual = dataStore.getDisplayEntitiesIn(
                playerHome.location.chunkX,
                playerHome.location.chunkZ,
            )
        )
        manager.despawnEntities(playerHome)
        assertTrue {
            entities.all { it.isDead }
        }
        assertEquals(
            expected = emptyList(),
            actual = dataStore.getDisplayEntitiesIn(
                playerHome.location.chunkX,
                playerHome.location.chunkZ,
            )
        )
    }
}