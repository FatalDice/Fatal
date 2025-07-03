package uk.akane.fatal.data.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import uk.akane.fatal.FatalPlugin
import uk.akane.fatal.data.database.group.GroupsTable
import uk.akane.fatal.data.database.profile.character.CharacterAttributesTable
import uk.akane.fatal.data.database.profile.character.CharacterSheetsTable
import uk.akane.fatal.data.database.profile.ProfilesTable
import uk.akane.fatal.data.database.universal.RulesetsIndexTable
import uk.akane.fatal.data.database.universal.RulesetsTable
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
            SchemaUtils.create(CharacterSheetsTable)
            SchemaUtils.create(CharacterAttributesTable)
            SchemaUtils.create(GroupsTable)
            SchemaUtils.create(RulesetsIndexTable)
            SchemaUtils.create(RulesetsTable)
        }
    }

    fun init() {
        database
    }
}