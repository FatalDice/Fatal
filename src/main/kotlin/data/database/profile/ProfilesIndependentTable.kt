package uk.akane.fatal.data.database.profile

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ProfilesIndependentTable : Table() {
    val userId = long("user_id")

    val defaultCharacterCardId = long("default_character_card_id")

    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(userId)
}