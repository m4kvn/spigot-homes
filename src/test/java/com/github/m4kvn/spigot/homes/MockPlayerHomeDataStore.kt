package com.github.m4kvn.spigot.homes

import com.github.m4kvn.spigot.homes.playerhome.local.PlayerHomeDataStore
import com.github.m4kvn.spigot.homes.playerhome.local.ProductionPlayerHomeDataStore
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection
import java.sql.DriverManager

class MockPlayerHomeDataStore(
    private val production: ProductionPlayerHomeDataStore,
) : PlayerHomeDataStore by production {
    private var connection: Connection? = null

    override fun connectDatabase() {
        val url = "jdbc:sqlite:file:test?mode=memory&cache=shared"
        connection = DriverManager.getConnection(url)
        production.database = Database.connect(url = url)
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE
    }

    override fun disconnectDatabase() {
        production.disconnectDatabase()
        connection?.close()
    }
}