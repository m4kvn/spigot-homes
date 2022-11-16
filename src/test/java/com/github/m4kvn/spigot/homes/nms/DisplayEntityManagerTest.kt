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
    fun デフォルトのホームでDisplayEntityを正しく生成し追加できる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val playerHome = world.newRandomPlayerHomeDefault(chunk = chunk)
        manager.addEntities(world, playerHome)
        val entities = dataStore.getDisplayEntities(playerHome)
        assertTrue { entities.all { !it.isDead } }
        assertTrue { entities.all { it.isVisible } }
        assertEquals(
            expected = 2,
            actual = entities.size,
        )
        assertEquals(
            expected = "${playerHome.owner.playerName}'s",
            actual = entities[0].text,
        )
        assertEquals(
            expected = "default home",
            actual = entities[1].text,
        )
    }

    @Test
    fun 名前付きのホームでDisplayEntityを正しく生成し追加できる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val playerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        manager.addEntities(world, playerHome)
        val entities = dataStore.getDisplayEntities(playerHome)
        assertTrue { entities.all { !it.isDead } }
        assertTrue { entities.all { it.isVisible } }
        assertEquals(
            expected = 3,
            actual = entities.size,
        )
        assertEquals(
            expected = "${playerHome.owner.playerName}'s",
            actual = entities[0].text,
        )
        assertEquals(
            expected = "home named",
            actual = entities[1].text,
        )
        assertEquals(
            expected = "<${playerHome.name}>",
            actual = entities[2].text,
        )
    }

    @Test
    fun 追加済みのホームデータを正しく削除できる() {
        val playerHome = world.newRandomPlayerHomeNamed()
        manager.addEntities(world, playerHome)
        assertTrue { dataStore.getDisplayEntities(playerHome).isNotEmpty() }
        manager.removeEntities(playerHome)
        assertEquals(
            expected = emptyList(),
            actual = dataStore.getDisplayEntities(playerHome)
        )
    }

    @Test
    fun 読み込まれていないChunkのDisplayEntityがspawnされない() {
        val chunk = world.newMockChunk(isLoaded = false)
        val defaultPlayerHome = world.newRandomPlayerHomeDefault(chunk = chunk)
        val namedPlayerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        manager.addEntities(world, defaultPlayerHome)
        manager.addEntities(world, namedPlayerHome)
        val entities = dataStore.getDisplayEntitiesIn(chunk.x, chunk.z)
        assertTrue { entities.isNotEmpty() }
        assertTrue { entities.all { it.isDead } }
    }

    @Test
    fun 読み込まれているChunkのDisplayEntityがspawnされる() {
        val chunk = world.newMockChunk(isLoaded = false)
        val defaultPlayerHome = world.newRandomPlayerHomeDefault(chunk = chunk)
        val namedPlayerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        manager.addEntities(world, defaultPlayerHome)
        manager.addEntities(world, namedPlayerHome)
        dataStore.getDisplayEntitiesIn(chunk.x, chunk.z).also {
            assertTrue { it.isNotEmpty() }
            assertTrue { it.all { it.isDead } }
        }
        manager.spawnEntitiesIn(chunk)
        dataStore.getDisplayEntitiesIn(chunk.x, chunk.z).also {
            assertTrue { it.isNotEmpty() }
            assertTrue { it.all { !it.isDead } }
        }
    }

    @Test
    fun 指定されたChunk内にある追加済みのDisplayEntityをdespawnできる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val defaultPlayerHome = world.newRandomPlayerHomeDefault(chunk = chunk)
        val namedPlayerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        manager.addEntities(world, defaultPlayerHome)
        manager.addEntities(world, namedPlayerHome)
        dataStore.getDisplayEntitiesIn(chunk.x, chunk.z).also {
            assertTrue { it.isNotEmpty() }
            assertTrue { it.all { !it.isDead } }
        }
        manager.despawnEntitiesIn(chunk)
        dataStore.getDisplayEntitiesIn(chunk.x, chunk.z).also {
            assertTrue { it.isNotEmpty() }
            assertTrue { it.all { it.isDead } }
        }
    }

    @Test
    fun 読み込まれていないChunkのDisplayEntityは追加されるがspawnされない() {
        val chunk = world.newMockChunk(isLoaded = false)
        val defaultPlayerHome = world.newRandomPlayerHomeDefault(chunk = chunk)
        val namedPlayerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        manager.addEntities(world, defaultPlayerHome)
        manager.addEntities(world, namedPlayerHome)
        val entities = dataStore.getDisplayEntitiesIn(chunk.x, chunk.z)
        assertTrue { entities.isNotEmpty() }
        assertTrue { entities.all { it.isDead } }
    }

    @Test
    fun 読み込まれているChunkのDisplayEntityが追加されspawnされる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val defaultPlayerHome = world.newRandomPlayerHomeDefault(chunk = chunk)
        val namedPlayerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        manager.addEntities(world, defaultPlayerHome)
        manager.addEntities(world, namedPlayerHome)
        val entities = dataStore.getDisplayEntitiesIn(chunk.x, chunk.z)
        assertTrue { entities.isNotEmpty() }
        assertTrue { entities.all { !it.isDead } }
    }

    @Test
    fun 既にspawnしているDisplayEntityをdespawnしてから削除できる() {
        val chunk = world.newMockChunk(isLoaded = true)
        val defaultPlayerHome = world.newRandomPlayerHomeDefault(chunk = chunk)
        val namedPlayerHome = world.newRandomPlayerHomeNamed(chunk = chunk)
        manager.addEntities(world, defaultPlayerHome)
        manager.addEntities(world, namedPlayerHome)
        dataStore.getDisplayEntitiesIn(chunk.x, chunk.z).also {
            assertTrue { it.isNotEmpty() }
            assertTrue { it.all { !it.isDead } }
        }
        manager.removeEntities(defaultPlayerHome)
        manager.removeEntities(namedPlayerHome)
        assertEquals(
            expected = emptyList(),
            actual = dataStore.getDisplayEntitiesIn(chunk.x, chunk.z),
        )
    }
}