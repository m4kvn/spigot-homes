package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.MockBukkitWrapper
import com.github.m4kvn.spigot.homes.asPlayerHomeChunk
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
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("NonAsciiCharacters", "TestFunctionName")
class ProductionDisplayEntityDataStoreTest : KoinTest {
    private val dataStore by inject<DisplayEntityDataStore>()
    private val bukkitWrapper by inject<MockBukkitWrapper>()

    private val testModule = module {
        single { MockBukkitWrapper() }
        single<DisplayEntityDataStore> { ProductionDisplayEntityDataStore() }
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
    fun 指定したChunk内のDisplayEntityを全て削除できる() {
        val world = bukkitWrapper.newMockWorld()
        val chunks = List(100) { world.newMockChunk() }.toMutableList()
        val playerHomes = chunks
            .map { chunk -> List(100) { world.newRandomPlayerHomeDefault(chunk = chunk) } }
            .flatten()
        playerHomes.forEach {
            val entities = newRandomDisplayEntities()
            dataStore.addDisplayEntities(it, entities)
        }
        assertTrue { // データが追加されていることを確認
            chunks.all {
                val homeChunk = it.asPlayerHomeChunk
                val entities = dataStore.getDisplayEntitiesIn(homeChunk)
                entities.isNotEmpty()
            }
        }

        val chunk = chunks[(0 until chunks.size).random()]
        val homeChunk = chunk.asPlayerHomeChunk
        dataStore.removeDisplayEntitiesIn(homeChunk)
        assertTrue { // Chunk内のデータが削除されているか
            val entities = dataStore.getDisplayEntitiesIn(homeChunk)
            entities.isEmpty()
        }
    }

    @Test
    fun 追加したDisplayEntityを正しく取得できる() {
        val world = bukkitWrapper.newMockWorld()
        val playerHome = world.newRandomPlayerHomeNamed()
        val entities = newRandomDisplayEntities()
        dataStore.addDisplayEntities(playerHome, entities)
        assertEquals(
            expected = entities,
            actual = dataStore.getDisplayEntitiesIn(
                playerHome.location.chunk
            )
        )
    }

    @Test
    fun 登録済みのDisplayEntityを削除できる() {
        val world = bukkitWrapper.newMockWorld()
        val playerHome = world.newRandomPlayerHomeNamed()
        val entities = newRandomDisplayEntities()
        dataStore.addDisplayEntities(playerHome, entities)
        assertEquals(
            expected = entities,
            actual = dataStore.getDisplayEntitiesIn(
                playerHome.location.chunk
            )
        )
        dataStore.removeDisplayEntities(playerHome)
        assertEquals(
            expected = emptyList(),
            actual = dataStore.getDisplayEntitiesIn(
                playerHome.location.chunk
            )
        )
    }

    @Test
    fun DisplayEntityを追加できる() {
        val world = bukkitWrapper.newMockWorld()
        val playerHome = world.newRandomPlayerHomeDefault()
        val entities = newRandomDisplayEntities()
        assertDoesNotThrow {
            dataStore.addDisplayEntities(playerHome, entities)
        }
        assertTrue {
            dataStore.getDisplayEntities(playerHome).isNotEmpty()
        }
        assertEquals(
            expected = entities,
            actual = dataStore.getDisplayEntities(playerHome),
        )
    }

    private fun newRandomDisplayEntities(): List<DisplayEntity> {
        return List(size = Random.nextInt(from = 1, until = 10)) { mock() }
    }
}