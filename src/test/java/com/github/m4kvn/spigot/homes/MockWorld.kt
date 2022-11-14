package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeOwner
import org.bukkit.Chunk
import org.bukkit.World
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
        owner: PlayerHomeOwner = newRandomPlayerHomeOwner(),
        location: PlayerHomeLocation = newRandomPlayerHomeLocation(),
    ) = PlayerHome.Default(
        owner = owner,
        location = location,
    )

    fun newRandomPlayerHomeNamed(
        chunk: Chunk = newMockChunk(),
        owner: PlayerHomeOwner = newRandomPlayerHomeOwner(),
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
        ownerUUID: UUID = UUID.randomUUID(),
        ownerName: String = "owner_name_${UUID.randomUUID()}",
    ) = PlayerHomeOwner(
        playerUUID = ownerUUID,
        playerName = ownerName,
    )

    private fun newRandomPlayerHomeLocation(
        chunk: Chunk = newMockChunk(),
    ) = PlayerHomeLocation(
        worldUUID = uid,
        worldName = getName(),
        locationX = Random.nextDouble(),
        locationY = Random.nextDouble(),
        locationZ = Random.nextDouble(),
        chunkX = chunk.x,
        chunkZ = chunk.z,
    )
}