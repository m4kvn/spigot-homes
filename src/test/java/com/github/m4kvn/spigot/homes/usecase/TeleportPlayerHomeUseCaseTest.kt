package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.MockBukkitWrapper
import com.github.m4kvn.spigot.homes.MockWorld
import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.playerhome.TemporaryPlayerHomeManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals

@Suppress("NonAsciiCharacters", "TestFunctionName")
class TeleportPlayerHomeUseCaseTest : KoinTest {
    private val world by inject<MockWorld>()
    private val temporaryPlayerHomeManager by inject<TemporaryPlayerHomeManager>()
    private val teleportPlayerHomeUseCase by inject<TeleportPlayerHomeUseCase>()

    private val testModule = module {
        val mockBukkitWrapper = MockBukkitWrapper()
        single<BukkitWrapper> { mockBukkitWrapper }
        single { mockBukkitWrapper.newMockWorld() }
        single { TemporaryPlayerHomeManager() }
        single { CreateTemporaryPlayerHomeUseCase() }
        single { SaveTemporaryPlayerHomeUseCase(get()) }
        single { TeleportPlayerHomeUseCase(get(), get(), get()) }
    }

    @BeforeEach
    fun setUp() {
        startKoin { modules(testModule) }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun 指定したホームにテレポートできる() {
        val player = world.newMockPlayer()
        val defaultHome = world.newRandomPlayerHomeDefault(player = player)
        teleportPlayerHomeUseCase(
            player = player,
            playerHome = defaultHome,
        )
        assertEquals(
            expected = defaultHome.location,
            actual = PlayerHomeLocation(
                worldUUID = world.uid,
                worldName = world.name,
                locationX = player.location.x,
                locationY = player.location.y,
                locationZ = player.location.z,
                locationYaw = player.location.yaw,
                locationPitch = player.location.pitch,
                chunkX = player.location.chunk.x,
                chunkZ = player.location.chunk.z,
            )
        )
    }

    @Test
    fun 指定したホームにテレポートしたときにテレポート前の場所が保存される() {
        val player = world.newMockPlayer()
        val previousPlayerHomeLocation = PlayerHomeLocation(
            worldUUID = world.uid,
            worldName = world.name,
            locationX = player.location.x,
            locationY = player.location.y,
            locationZ = player.location.z,
            locationYaw = player.location.yaw,
            locationPitch = player.location.pitch,
            chunkX = player.location.chunk.x,
            chunkZ = player.location.chunk.z,
        )
        teleportPlayerHomeUseCase(
            player = player,
            playerHome = world.newRandomPlayerHomeDefault(player = player),
            savePrevious = true,
        )
        assertEquals(
            expected = previousPlayerHomeLocation,
            actual = temporaryPlayerHomeManager.get(player.uniqueId)?.location,
        )
    }

    @Test
    fun 指定したホームにテレポートしたときにテレポート前の場所を保存しない選択ができる() {
        val player = world.newMockPlayer()
        teleportPlayerHomeUseCase(
            player = player,
            playerHome = world.newRandomPlayerHomeDefault(player = player),
            savePrevious = false,
        )
        assertEquals(
            expected = null,
            actual = temporaryPlayerHomeManager.get(player.uniqueId),
        )
    }
}