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
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@Suppress("NonAsciiCharacters", "TestFunctionName")
internal class SetNamedPlayerHomeUseCaseTest : KoinTest {
    private val world by inject<MockWorld>()
    private val homeManager by inject<PlayerHomeManager>()
    private val homeDataStore by inject<PlayerHomeDataStore>()
    private val displayStore by inject<DisplayEntityDataStore>()
    private val displayManager by inject<DisplayEntityManager>()
    private val setNamedPlayerHomeUseCase by inject<SetNamedPlayerHomeUseCase>()
    private val createNamedPlayerHomeUseCase by inject<CreateNamedPlayerHomeUseCase>()


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
        single { CreateNamedPlayerHomeUseCase() }
        single { SetNamedPlayerHomeUseCase(get(), get(), get()) }
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
    fun 新しい名前付きホームを設定できる() {
        val player = world.newMockPlayer()
        val homeName = "home_name_${player.uniqueId}"
        setNamedPlayerHomeUseCase(player, homeName)
        assertNotEquals(
            illegal = null,
            actual = homeManager.getNamedHome(player.uniqueId, homeName),
        )
    }

    @Test
    fun 同じ名前の名前付きホームを設定した場合に既存の名前付きホームを削除する() {
        val player = world.newMockPlayer()
        val playerHome = world.newRandomPlayerHomeNamed(player = player)
        homeManager.addNamedHome(playerHome)
        setNamedPlayerHomeUseCase(player, playerHome.name)
        assertNotEquals(
            illegal = playerHome,
            actual = homeManager.getNamedHome(player.uniqueId, playerHome.name)
        )
    }

    @Test
    fun 新しい名前付きホームを設定したときDisplayEntityを追加する() {
        val homeName = "home_name_${Random.nextInt()}"
        val chunk = world.newMockChunk(isLoaded = true)
        val location = world.newMockLocation(locationChunk = chunk)
        val player = world.newMockPlayer(playerLocation = location)
        val newNamedHome = setNamedPlayerHomeUseCase(player, homeName)
        val entities = displayStore.getDisplayEntities(newNamedHome)
        assertTrue { entities.isNotEmpty() }
        assertTrue { entities.all { !it.isDead } }
    }

    @Test
    fun 同じ名前の名前付きホームを設定したときに既存のDisplayEntityを削除する() {
        val homeName = "home_name_${Random.nextInt()}"
        val playerUUID = UUID.randomUUID()
        val oldLocationPlayer = world.newMockPlayer(playerUUID = playerUUID)
        val oldNamedHome = createNamedPlayerHomeUseCase(oldLocationPlayer, homeName)
        homeManager.addNamedHome(oldNamedHome)
        displayManager.addEntities(world, oldNamedHome)
        val oldDisplays = displayStore.getDisplayEntities(oldNamedHome)
        assertTrue { oldDisplays.isNotEmpty() }
        val newLocationPlayer = world.newMockPlayer(playerUUID = playerUUID)
        setNamedPlayerHomeUseCase(newLocationPlayer, homeName)
        assertEquals(
            expected = emptyList(),
            actual = displayStore.getDisplayEntities(oldNamedHome)
        )
    }
}