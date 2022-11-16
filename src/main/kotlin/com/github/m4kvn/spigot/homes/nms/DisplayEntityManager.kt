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

    fun createEntities(
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

    fun addEntities(playerHome: PlayerHome, entities: List<DisplayEntity>) {
        dataStore.addDisplayEntities(
            playerHome = playerHome,
            entities = entities,
        )
    }

    fun removeEntities(playerHome: PlayerHome) {
        dataStore.removeDisplayEntities(playerHome)
    }

    fun spawnEntitiesIn(chunk: Chunk) {
        if (!chunk.isLoaded) return
        dataStore
            .getDisplayEntitiesIn(chunk.x, chunk.z)
            .forEach { it.isDead = false }
    }

    fun despawnEntitiesIn(chunk: Chunk) {
        dataStore
            .getDisplayEntitiesIn(chunk.x, chunk.z)
            .forEach { it.isDead = true }
    }

    fun spawnEntities(world: World, playerHome: PlayerHome) {
        val entities = createEntities(world, playerHome)
        addEntities(playerHome, entities)
        val chunk = world.getChunkAt(
            playerHome.location.chunkX,
            playerHome.location.chunkZ,
        )
        spawnEntitiesIn(chunk)
    }

    fun spawnEntities(playerHomeList: List<PlayerHome>) {
        playerHomeList.forEach {
            val world = bukkit.getWorld(it.location.worldUUID) ?: return@forEach
            spawnEntities(world, it)
        }
    }

    fun despawnEntities(playerHome: PlayerHome) {
        val entities = dataStore.getDisplayEntities(playerHome)
        entities.forEach { it.isDead = true }
        removeEntities(playerHome)
    }

    fun despawnEntities(playerHomeList: List<PlayerHome>) {
        playerHomeList.forEach { despawnEntities(it) }
    }

    private val PlayerHome.displayTextList: List<String>
        get() = when (this) {
            is PlayerHome.Default -> "${owner.playerName}'s\ndefault home"
            is PlayerHome.Named -> "${owner.playerName}'s\nhome named\n<$name>"
        }.split("\n")
}