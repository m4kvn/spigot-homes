package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.model.PlayerHome
import com.github.m4kvn.spigot.homes.model.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.model.PlayerHomeOwner
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

@Suppress("NonAsciiCharacters", "TestFunctionName")
class DisplayEntityDataStoreTest : KoinTest {
    private val dataStore by inject<DisplayEntityDataStore>()

    private val testModule = module {
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
        val owner = PlayerHomeOwner(
            playerUUID = UUID.randomUUID(),
            playerName = "player",
        )
        val location = PlayerHomeLocation(
            worldUUID = UUID.randomUUID(),
            worldName = "world",
            locationX = Random.nextDouble(),
            locationY = Random.nextDouble(),
            locationZ = Random.nextDouble(),
            chunkX = Random.nextInt(),
            chunkZ = Random.nextInt(),
        )
        val playerHome = PlayerHome.Default(
            owner = owner,
            location = location,
        )
        val entities = listOf<DisplayEntity>(mock(), mock())
        dataStore.addDisplayEntities(playerHome, entities)

        val actual = dataStore.getDisplayEntitiesIn(
            chunkX = location.chunkX,
            chunkZ = location.chunkZ,
        )
        assertEquals(entities, actual)
    }
}