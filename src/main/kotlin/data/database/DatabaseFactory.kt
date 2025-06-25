package uk.akane.fatal.data.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import uk.akane.fatal.FatalPlugin
import java.io.File

object DatabaseFactory {
    val database by lazy {
        val dbPath = File(FatalPlugin.dataFolder, "fatal.db").absolutePath
        Database.connect("jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(ProfilesTable)
        }
    }

    fun init() {
        database
    }
}