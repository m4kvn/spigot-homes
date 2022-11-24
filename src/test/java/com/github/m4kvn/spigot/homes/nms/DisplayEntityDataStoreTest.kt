package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.MockBukkitWrapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.kotlin.mock
import kotlin.random.Random
import kotlin.test.assertEquals

@Suppress("NonAsciiCharacters", "TestFunctionName")
class DisplayEntityDataStoreTest : KoinTest {
    private val dataStore by inject<DisplayEntityDataStore>()
    private val bukkitWrapper by inject<MockBukkitWrapper>()

    private val testModule = module {
        single { MockBukkitWrapper() }
        single { DisplayEntityDataStore() }
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
    fun 追加したDisplayEntityを正しく取得できる() {
        val world = bukkitWrapper.newMockWorld()
        val playerHome = world.newRandomPlayerHomeNamed()
        val entities = newRandomDisplayEntities()
        dataStore.addDisplayEntities(playerHome, entities)
        assertEquals(
            expected = entities,
            actual = dataStore.getDisplayEntitiesIn(
                chunkX = playerHome.location.chunkX,
                chunkZ = playerHome.location.chunkZ,
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
                chunkX = playerHome.location.chunkX,
                chunkZ = playerHome.location.chunkZ,
            )
        )
        dataStore.removeDisplayEntities(playerHome)
        assertEquals(
            expected = emptyList(),
            actual = dataStore.getDisplayEntitiesIn(
                chunkX = playerHome.location.chunkX,
                chunkZ = playerHome.location.chunkZ,
            )
        )
    }

    private fun newRandomDisplayEntities(): List<DisplayEntity> {
        return List(size = Random.nextInt(from = 1, until = 10)) { mock() }
    }
}