package uk.akane.fatal.data.database.profile.character

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object CharacterAttributesTable : Table() {
    val id = integer("id").autoIncrement()
    val characterCardId = integer("character_card_id")

    val attributeName = varchar("attribute_name", 64)
    val successRate = long("success_rate")

    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(id)
    val characterCardForeignKey = reference("character_card_id", CharacterCardsTable.id)
}