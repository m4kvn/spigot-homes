package com.github.m4kvn.spigot.homes.nms

import com.github.m4kvn.spigot.homes.asPlayerHomeChunk
import com.github.m4kvn.spigot.homes.bukkit.BukkitWrapper
import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import org.bukkit.Chunk
import org.bukkit.World

class ProductionDisplayEntityManager(
    private val nms: NmsWrapper,
    private val bukkit: BukkitWrapper,
    private val dataStore: DisplayEntityDataStore,
): DisplayEntityManager {

    override fun spawnEntities(chunk: Chunk, playerHomeList: List<PlayerHome>) {
        playerHomeList.forEach { home ->
            spawnEntities(chunk.world, home)
        }
    }

    override fun spawnEntities(world: World, playerHome: PlayerHome) {
        val location = bukkit.getLocation(playerHome)
        if (!location.chunk.isLoaded) return
        val entities = createEntities(world, playerHome)
        dataStore.addDisplayEntities(
            home = playerHome,
            entities = entities,
        )
    }

    override fun despawnEntities(chunk: Chunk) {
        val homeChunk = chunk.asPlayerHomeChunk
        val entities = dataStore.removeDisplayEntitiesIn(homeChunk)
        entities.forEach { it.dead() }
    }

    override fun despawnAllEntities() {
        val entities = dataStore.removeAllDisplayEntities()
        entities.forEach { it.dead() }
    }

    override fun despawnEntities(playerHome: PlayerHome) {
        val entities = dataStore.removeDisplayEntities(playerHome)
        entities.forEach { it.dead() }
    }

    override fun createEntities(world: World, playerHome: PlayerHome): List<DisplayEntity> {
        val displayTextList = when (playerHome) {
            is PlayerHome.Default -> "${playerHome.owner.playerName}'s\ndefault home"
            is PlayerHome.Named -> "${playerHome.owner.playerName}'s\nhome named\n<${playerHome.name}>"
            is PlayerHome.Temporary -> null
        }?.split("\n") ?: return emptyList()
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