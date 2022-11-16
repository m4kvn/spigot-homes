package com.github.m4kvn.spigot.homes.usecase

import com.github.m4kvn.spigot.homes.MockBukkitWrapper
import com.github.m4kvn.spigot.homes.MockNmsWrapper
import com.github.m4kvn.spigot.homes.MockPlayerHomeDataStore
import com.github.m4kvn.spigot.homes.MockWorld
import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.nms.DisplayEntityDataStore
import com.github.m4kvn.spigot.homes.nms.DisplayEntityManager
import com.github.m4kvn.spigot.homes.nms.NmsWrapper
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeManager
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
    private val createDefaultPlayerHomeUseCase by inject<CreateDefaultPlayerHomeUseCase>()

    private val testModule = module {
        single<JavaPlugin> { mock() }
        single<NmsWrapper> { MockNmsWrapper() }
        single<BukkitWrapper> { MockBukkitWrapper() }
        single<PlayerHomeDataStore> { MockPlayerHomeDataStore(get()) }
        single { MockWorld() }
        single { DisplayEntityDataStore() }
        single { DisplayEntityManager(get(), get(), get()) }
        single { PlayerHomeManager(get()) }
        single { ProductionPlayerHomeDataStore(get()) }
        single { CreateDefaultPlayerHomeUseCase() }
        single { SetDefaultPlayerHomeUseCase(get(), get(), get()) }
    }

    @BeforeEach
    fun setUp() {
        startKoin { modules(testModule) }
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
        assertTrue { entities.all { !it.isDead } }
    }

    @Test
    fun 新しいデフォルトホームを設定したときに既存のデフォルトホームのDisplayEntityを削除する() {
        val playerUUID = UUID.randomUUID()
        val oldLocationPlayer = world.newMockPlayer(playerUUID = playerUUID)
        val oldDefaultHome = createDefaultPlayerHomeUseCase(player = oldLocationPlayer)
        homeManager.addDefaultHome(oldDefaultHome)
        displayManager.addEntities(world, oldDefaultHome)
        val oldDisplays = displayStore.getDisplayEntities(oldDefaultHome)
        assertTrue { oldDisplays.isNotEmpty() }
        val newLocationPlayer = world.newMockPlayer(playerUUID = playerUUID)
        setDefaultPlayerHomeUseCase(newLocationPlayer)
        assertEquals(
            expected = emptyList(),
            actual = displayStore.getDisplayEntities(oldDefaultHome)
        )
    }
}