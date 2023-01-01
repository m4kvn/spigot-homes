package com.github.m4kvn.spigot.homes.playerhome.local

import com.github.m4kvn.spigot.homes.playerhome.PlayerHome
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeChunk
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeLocation
import com.github.m4kvn.spigot.homes.playerhome.PlayerHomeOwner
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

class ProductionPlayerHomeDataStore(
    private val plugin: JavaPlugin,
) : PlayerHomeDataStore {
    override var database: Database? = null

    override fun connectDatabase() {
        val filePath = "${plugin.dataFolder}/data_player_home.db"
        val file = File(filePath).apply {
            if (!parentFile.exists()) parentFile.mkdirs()
            if (!exists()) createNewFile()
        }
        database = Database.connect(
            url = "jdbc:sqlite:${file.path}",
            driver = "org.sqlite.JDBC",
        )
    }

    override fun disconnectDatabase() {
        database?.let {
            TransactionManager.closeAndUnregister(it)
        }
    }

    override fun createTables() {
        transaction(db = database) {
            SchemaUtils.create(
                LocalPlayerHomeOwner,
                LocalPlayerHomeDefault,
                LocalPlayerHomeNamed,
            )
            SchemaUtils.createMissingTablesAndColumns(
                LocalPlayerHomeDefault,
                LocalPlayerHomeNamed,
            )
        }
    }

    override fun storeOwnerList(ownerList: List<PlayerHomeOwner>) {
        transaction(db = database) {
            LocalPlayerHomeOwner.deleteAll()
            LocalPlayerHomeOwner.batchInsert(
                data = ownerList,
                shouldReturnGeneratedValues = false,
            ) { owner ->
                this[LocalPlayerHomeOwner.ownerUUID] = owner.playerUUID.toString()
                this[LocalPlayerHomeOwner.ownerName] = owner.playerName
            }
        }
    }

    override fun restoreOwnerList(): List<PlayerHomeOwner> {
        return transaction(db = database) {
            LocalPlayerHomeOwner.selectAll().map {
                PlayerHomeOwner(
                    playerUUID = UUID.fromString(it[LocalPlayerHomeOwner.ownerUUID]),
                    playerName = it[LocalPlayerHomeOwner.ownerName],
                )
            }
        }
    }

    override fun storeDefaultHomeList(playerHomeList: List<PlayerHome.Default>) {
        transaction(db = database) {
            LocalPlayerHomeDefault.deleteAll()
            LocalPlayerHomeDefault.batchInsert(
                data = playerHomeList,
                shouldReturnGeneratedValues = false,
            ) { playerHome ->
                this[LocalPlayerHomeDefault.ownerUUID] = playerHome.owner.playerUUID.toString()
                this[LocalPlayerHomeDefault.ownerName] = playerHome.owner.playerName
                this[LocalPlayerHomeDefault.worldUUID] = playerHome.location.worldUUID.toString()
                this[LocalPlayerHomeDefault.worldName] = playerHome.location.worldName
                this[LocalPlayerHomeDefault.locationX] = playerHome.location.locationX
                this[LocalPlayerHomeDefault.locationY] = playerHome.location.locationY
                this[LocalPlayerHomeDefault.locationZ] = playerHome.location.locationZ
                this[LocalPlayerHomeDefault.locationYaw] = playerHome.location.locationYaw
                this[LocalPlayerHomeDefault.locationPitch] = playerHome.location.locationPitch
                this[LocalPlayerHomeDefault.chunkX] = playerHome.location.chunk.x
                this[LocalPlayerHomeDefault.chunkZ] = playerHome.location.chunk.z
                this[LocalPlayerHomeDefault.isPrivate] = playerHome.isPrivate
            }
        }
    }

    override fun restoreDefaultHomeList(): List<PlayerHome.Default> {
        return transaction(db = database) {
            LocalPlayerHomeDefault.selectAll().map {
                PlayerHome.Default(
                    owner = PlayerHomeOwner(
                        playerUUID = UUID.fromString(it[LocalPlayerHomeDefault.ownerUUID]),
                        playerName = it[LocalPlayerHomeDefault.ownerName],
                    ),
                    location = PlayerHomeLocation(
                        worldUUID = UUID.fromString(it[LocalPlayerHomeDefault.worldUUID]),
                        worldName = it[LocalPlayerHomeDefault.worldName],
                        locationX = it[LocalPlayerHomeDefault.locationX],
                        locationY = it[LocalPlayerHomeDefault.locationY],
                        locationZ = it[LocalPlayerHomeDefault.locationZ],
                        locationYaw = it[LocalPlayerHomeDefault.locationYaw],
                        locationPitch = it[LocalPlayerHomeDefault.locationPitch],
                        chunk = PlayerHomeChunk(
                            x = it[LocalPlayerHomeDefault.chunkX],
                            z = it[LocalPlayerHomeDefault.chunkZ],
                            worldUID = UUID.fromString(it[LocalPlayerHomeDefault.worldUUID]),
                        ),
                    ),
                    isPrivate = it[LocalPlayerHomeDefault.isPrivate],
                )
            }
        }
    }

    override fun storeNamedHomeList(playerHomeList: List<PlayerHome.Named>) {
        transaction(db = database) {
            LocalPlayerHomeNamed.deleteAll()
            LocalPlayerHomeNamed.batchInsert(
                data = playerHomeList,
                shouldReturnGeneratedValues = false,
            ) { playerHome ->
                this[LocalPlayerHomeNamed.ownerUUID] = playerHome.owner.playerUUID.toString()
                this[LocalPlayerHomeNamed.ownerName] = playerHome.owner.playerName
                this[LocalPlayerHomeNamed.worldUUID] = playerHome.location.worldUUID.toString()
                this[LocalPlayerHomeNamed.worldName] = playerHome.location.worldName
                this[LocalPlayerHomeNamed.locationX] = playerHome.location.locationX
                this[LocalPlayerHomeNamed.locationY] = playerHome.location.locationY
                this[LocalPlayerHomeNamed.locationZ] = playerHome.location.locationZ
                this[LocalPlayerHomeNamed.locationYaw] = playerHome.location.locationYaw
                this[LocalPlayerHomeNamed.locationPitch] = playerHome.location.locationPitch
                this[LocalPlayerHomeNamed.chunkX] = playerHome.location.chunk.x
                this[LocalPlayerHomeNamed.chunkZ] = playerHome.location.chunk.z
                this[LocalPlayerHomeNamed.isPrivate] = playerHome.isPrivate
                this[LocalPlayerHomeNamed.homeName] = playerHome.name
            }
        }
    }

    override fun restoreNamedHomeList(ownerUUID: String): List<PlayerHome.Named> {
        return transaction(db = database) {
            LocalPlayerHomeNamed
                .select { LocalPlayerHomeNamed.ownerUUID eq ownerUUID }
                .map {
                    PlayerHome.Named(
                        owner = PlayerHomeOwner(
                            playerUUID = UUID.fromString(it[LocalPlayerHomeNamed.ownerUUID]),
                            playerName = it[LocalPlayerHomeNamed.ownerName],
                        ),
                        location = PlayerHomeLocation(
                            worldUUID = UUID.fromString(it[LocalPlayerHomeNamed.worldUUID]),
                            worldName = it[LocalPlayerHomeNamed.worldName],
                            locationX = it[LocalPlayerHomeNamed.locationX],
                            locationY = it[LocalPlayerHomeNamed.locationY],
                            locationZ = it[LocalPlayerHomeNamed.locationZ],
                            locationPitch = it[LocalPlayerHomeNamed.locationPitch],
                            locationYaw = it[LocalPlayerHomeNamed.locationYaw],
                            chunk = PlayerHomeChunk(
                                x = it[LocalPlayerHomeNamed.chunkX],
                                z = it[LocalPlayerHomeNamed.chunkZ],
                                worldUID = UUID.fromString(it[LocalPlayerHomeNamed.worldUUID]),
                            ),
                        ),
                        isPrivate = it[LocalPlayerHomeNamed.isPrivate],
                        name = it[LocalPlayerHomeNamed.homeName],
                    )
                }
        }
    }
}