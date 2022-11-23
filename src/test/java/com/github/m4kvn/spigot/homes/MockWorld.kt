package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeOwner
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.random.Random

class MockWorld(
    private val uuid: UUID = UUID.randomUUID(),
    private val name: String = "mock_world",
) : World by mock() {
    private val chunkMap = hashMapOf<Pair<Int, Int>, Chunk>()

    override fun getUID(): UUID = uuid

    override fun getName(): String = name

    override fun getChunkAt(x: Int, z: Int): Chunk {
        return requireNotNull(chunkMap[x to z])
    }

    fun newMockPlayer(
        playerUUID: UUID = UUID.randomUUID(),
        playerName: String = "player_name_$playerUUID",
        playerLocation: Location = newMockLocation(),
    ) = mock<Player>().apply {
        whenever(world) doReturn this@MockWorld
        whenever(name) doReturn playerName
        whenever(uniqueId) doReturn playerUUID
        whenever(location) doReturn playerLocation
    }

    fun newMockChunk(
        x: Int = Random.nextInt(),
        z: Int = Random.nextInt(),
        isLoaded: Boolean = false,
    ): Chunk {
        val chunk = mock<Chunk>().apply {
            whenever(isLoaded()) doReturn isLoaded
            whenever(getX()) doReturn x
            whenever(getZ()) doReturn z
            whenever(world) doReturn this@MockWorld
        }
        chunkMap[x to z] = chunk
        return chunk
    }

    fun newRandomPlayerHomeDefault(
        chunk: Chunk = newMockChunk(),
        player: Player = newMockPlayer(),
        owner: PlayerHomeOwner = newRandomPlayerHomeOwner(player),
        location: PlayerHomeLocation = newRandomPlayerHomeLocation(
            chunk = chunk,
        ),
    ) = PlayerHome.Default(
        owner = owner,
        location = location,
    )

    fun newRandomPlayerHomeNamed(
        chunk: Chunk = newMockChunk(),
        player: Player = newMockPlayer(),
        owner: PlayerHomeOwner = newRandomPlayerHomeOwner(player),
        location: PlayerHomeLocation = newRandomPlayerHomeLocation(
            chunk = chunk,
        ),
        homeName: String = "home_name_${UUID.randomUUID()}",
    ) = PlayerHome.Named(
        owner = owner,
        location = location,
        name = homeName,
    )

    fun newRandomPlayerHomeOwner(
        player: Player = newMockPlayer(),
    ) = PlayerHomeOwner(
        playerUUID = player.uniqueId,
        playerName = player.name,
    )

    fun newMockLocation(
        locationChunk: Chunk = newMockChunk(),
    ) = mock<Location>().apply {
        whenever(x) doReturn Random.nextDouble()
        whenever(y) doReturn Random.nextDouble()
        whenever(z) doReturn Random.nextDouble()
        whenever(pitch) doReturn Random.nextFloat()
        whenever(yaw) doReturn Random.nextFloat()
        whenever(world) doReturn this@MockWorld
        whenever(chunk) doReturn locationChunk
    }

    private fun newRandomPlayerHomeLocation(
        chunk: Chunk = newMockChunk(),
    ) = PlayerHomeLocation(
        worldUUID = uid,
        worldName = getName(),
        locationX = Random.nextDouble(),
        locationY = Random.nextDouble(),
        locationZ = Random.nextDouble(),
        locationPitch = Random.nextFloat(),
        locationYaw = Random.nextFloat(),
        chunkX = chunk.x,
        chunkZ = chunk.z,
    )
}