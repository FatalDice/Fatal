package uk.akane.fatal.data.database.profile.character

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import uk.akane.fatal.data.database.profile.ProfilesIndependentTable
import java.time.LocalDateTime

object CharacterDao {

    fun createCharacterCard(userId: Long, name: String, description: String) {
        transaction {
            val createdAt = LocalDateTime.now()
            CharacterCardsTable
                .insert {
                    it[CharacterCardsTable.userId] = userId
                    it[CharacterCardsTable.name] = name
                    it[CharacterCardsTable.description] = description
                    it[CharacterCardsTable.createdAt] = createdAt
                    it[updatedAt] = createdAt
                }
        }
    }

    fun updateCharacterCard(id: Int, userId: Long, name: String?, description: String?): Boolean {
        return transaction {
            val updatedCount = CharacterCardsTable
                .update({ CharacterCardsTable.id eq id and (CharacterCardsTable.userId eq userId) }) {
                    it[CharacterCardsTable.name] = name ?: "DefaultName"
                    it[CharacterCardsTable.description] = description ?: "DefaultDescription"
                    it[updatedAt] = LocalDateTime.now()
                }
            updatedCount > 0
        }
    }

    fun getCharacterCard(id: Int): ResultRow? {
        return transaction {
            CharacterCardsTable
                .selectAll()
                .where { CharacterCardsTable.id eq id }
                .singleOrNull()
        }
    }

    fun getCharacterCardsByUser(userId: Long): List<ResultRow> {
        return transaction {
            CharacterCardsTable
                .selectAll()
                .where { CharacterCardsTable.userId eq userId }
                .toList()
        }
    }

    fun deleteCharacterCard(id: Int): Boolean {
        return transaction {
            val deletedCount = CharacterCardsTable.deleteWhere { CharacterCardsTable.id eq id }
            deletedCount > 0
        }
    }

    fun createCharacterAttribute(characterCardId: Int, attributeName: String, successRate: Long) {
        return transaction {
            val createdAt = LocalDateTime.now()
            CharacterAttributesTable
                .insert {
                    it[CharacterAttributesTable.characterCardId] = characterCardId
                    it[CharacterAttributesTable.attributeName] = attributeName
                    it[CharacterAttributesTable.successRate] = successRate
                    it[CharacterAttributesTable.createdAt] = createdAt
                    it[updatedAt] = createdAt
                }
        }
    }

    fun updateCharacterAttribute(id: Int, characterCardId: Int, attributeName: String?, successRate: Long?): Boolean {
        return transaction {
            val updatedCount = CharacterAttributesTable
                .update({ CharacterAttributesTable.id eq id and (CharacterAttributesTable.characterCardId eq characterCardId) }) {
                    it[CharacterAttributesTable.attributeName] = attributeName ?: "UnknownAttribute"
                    it[CharacterAttributesTable.successRate] = successRate ?: 0
                    it[updatedAt] = LocalDateTime.now()
                }
            updatedCount > 0
        }
    }

    fun getCharacterAttribute(id: Int): ResultRow? {
        return transaction {
            CharacterAttributesTable
                .selectAll()
                .where { CharacterAttributesTable.id eq id }
                .singleOrNull()
        }
    }

    fun getCharacterAttributesByCard(characterCardId: Int): List<ResultRow> {
        return transaction {
            CharacterAttributesTable
                .selectAll()
                .where { CharacterAttributesTable.characterCardId eq characterCardId }
                .toList()
        }
    }

    fun deleteCharacterAttribute(id: Int): Boolean {
        return transaction {
            val deletedCount = CharacterAttributesTable.deleteWhere { CharacterAttributesTable.id eq id }
            deletedCount > 0
        }
    }

    fun getDefaultCharacterCardId(userId: Long): Long? {
        return transaction {
            ProfilesIndependentTable
                .selectAll()
                .where { ProfilesIndependentTable.userId eq userId }
                .singleOrNull()
                ?.get(ProfilesIndependentTable.defaultCharacterCardId)
        }
    }

    fun setDefaultCharacterCardId(userId: Long, defaultCharacterCardId: Long): Boolean {
        return transaction {
            val updatedCount = ProfilesIndependentTable
                .update({ ProfilesIndependentTable.userId eq userId }) {
                    it[ProfilesIndependentTable.defaultCharacterCardId] = defaultCharacterCardId
                }
            updatedCount > 0
        }
    }
}
