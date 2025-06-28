package uk.akane.fatal.data.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import uk.akane.fatal.FatalPlugin
import java.io.File

object DatabaseFactory {
    val database by lazy {
        val dbPath = File(FatalPlugin.dataFolder, "fatal.db").absolutePath
        Database.connect(
            "jdbc:sqlite:$dbPath",
            "org.sqlite.JDBC",
            setupConnection = { connection ->
                connection.createStatement().executeUpdate("PRAGMA foreign_keys = ON")
            }
        )
        transaction {
            SchemaUtils.create(ProfilesTable)
            SchemaUtils.create(GroupsTable)
            SchemaUtils.create(RulesetsIndexTable)
            SchemaUtils.create(RulesetsTable)
        }
    }

    fun init() {
        database
    }
}