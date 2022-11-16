package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import org.bukkit.Chunk
import org.bukkit.World

class DisplayEntityManager(
    private val nms: NmsWrapper,
    private val bukkit: BukkitWrapper,
    private val dataStore: DisplayEntityDataStore,
) {

    private val PlayerHome.displayTextList: List<String>
        get() = when (this) {
            is PlayerHome.Default -> "${owner.playerName}'s\ndefault home"
            is PlayerHome.Named -> "${owner.playerName}'s\nhome named\n<$name>"
        }.split("\n")

    fun spawnEntitiesIn(chunk: Chunk) {
        dataStore
            .getDisplayEntitiesIn(chunk.x, chunk.z)
            .forEach { it.isDead = false }
    }

    fun despawnEntitiesIn(chunk: Chunk) {
        dataStore
            .getDisplayEntitiesIn(chunk.x, chunk.z)
            .forEach { it.isDead = true }
    }

    fun addEntities(playerHomeList: List<PlayerHome>) {
        playerHomeList.forEach { playerHome ->
            val world = bukkit.getWorld(playerHome.location.worldUUID) ?: return@forEach
            addEntities(world, playerHome)
        }
    }

    fun addEntities(world: World, playerHome: PlayerHome) {
        val entities = createEntities(world, playerHome)
        dataStore.addDisplayEntities(
            playerHome = playerHome,
            entities = entities,
        )
        spawnEntities(world, playerHome)
    }

    fun removeEntities(playerHomeList: List<PlayerHome>) {
        playerHomeList.forEach { playerHome ->
            removeEntities(playerHome)
        }
    }

    fun removeEntities(playerHome: PlayerHome) {
        despawnEntities(playerHome)
        dataStore.removeDisplayEntities(playerHome)
    }

    private fun spawnEntities(world: World, playerHome: PlayerHome) {
        val chunk = world.getChunkAt(
            playerHome.location.chunkX,
            playerHome.location.chunkZ,
        )
        if (!chunk.isLoaded) return
        dataStore
            .getDisplayEntities(playerHome)
            .forEach { it.isDead = false }
    }

    private fun despawnEntities(playerHome: PlayerHome) {
        val entities = dataStore.getDisplayEntities(playerHome)
        entities.forEach { it.isDead = true }
    }

    private fun createEntities(
        world: World,
        playerHome: PlayerHome,
    ): List<DisplayEntity> {
        val displayTextList = playerHome.displayTextList
        return displayTextList.mapIndexed { index, displayText ->
            nms.newDisplayEntity(
                world = world,
                text = displayText,
                isVisible = !playerHome.isPrivate,
                location = DisplayEntityLocation(
                    x = playerHome.location.locationX,
                    y = playerHome.location.locationY + 0.9 - (index * 0.3),
                    z = playerHome.location.locationZ,
                )
            )
        }
    }
}