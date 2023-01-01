package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeChunk
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
    private val uuid: UUID,
    private val name: String,
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
    ) = MockPlayer(
        initialWorld = this,
        initialLocation = playerLocation,
        playerUUID = playerUUID,
        playerName = playerName,
    )

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
        locationX: Double = Random.nextDouble(),
        locationY: Double = Random.nextDouble(),
        locationZ: Double = Random.nextDouble(),
        locationPitch: Float = Random.nextFloat(),
        locationYaw: Float = Random.nextFloat(),
    ) = mock<Location>().apply {
        whenever(x) doReturn locationX
        whenever(y) doReturn locationY
        whenever(z) doReturn locationZ
        whenever(pitch) doReturn locationPitch
        whenever(yaw) doReturn locationYaw
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
        chunk = PlayerHomeChunk(chunk.x, chunk.z, chunk.world.uid)
    )
}