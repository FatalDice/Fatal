package uk.akane.fatal.data.database.profile.character

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object CharacterSheetsTable : Table() {
    val id = long("id").autoIncrement()
    val userId = long("user_id")

    val name = varchar("name", 128)
    val description = text("description")

    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
}