package uk.akane.fatal.data.database.profile.character

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import uk.akane.fatal.data.database.profile.ProfilesTable
import uk.akane.fatal.utils.CharacterSheetAttributeNotFoundException
import uk.akane.fatal.utils.CharacterSheetNoDefaultException
import uk.akane.fatal.utils.CharacterSheetNotFoundException
import java.time.LocalDateTime

object CharacterDao {

    fun createCharacterSheet(userId: Long, name: String, description: String) {
        transaction {
            val createdAt = LocalDateTime.now()
            CharacterSheetsTable
                .insert {
                    it[this.userId] = userId
                    it[this.name] = name
                    it[this.description] = description
                    it[this.createdAt] = createdAt
                    it[this.updatedAt] = createdAt
                }
        }
    }

    fun deleteCharacterSheet(userId: Long, characterCardName: String) {
        transaction {
            val characterSheetId = getCharacterSheetIdByName(userId, characterCardName)

            // First, check if it's the default character card for the user
            val defaultCharacterSheetId = getDefaultCharacterSheet(userId)

            if (defaultCharacterSheetId == characterSheetId) {
                ProfilesTable.update({ (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq 0L) }) {
                    it[ProfilesTable.defaultCharacterSheetId] = null
                }
            }

            ProfilesTable
                .selectAll()
                .where { ProfilesTable.userId eq userId }
                .forEach { row ->
                    if (row[ProfilesTable.selectedCharacterSheetId] == characterSheetId) {
                        ProfilesTable.update({
                            (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq row[ProfilesTable.groupId])
                        }) {
                            it[ProfilesTable.selectedCharacterSheetId] = null
                        }
                    }
                }

            CharacterSheetsTable.deleteWhere { CharacterSheetsTable.id eq characterSheetId }
            CharacterAttributesTable.deleteWhere { CharacterAttributesTable.CharacterSheetId eq characterSheetId }
        }
    }

    fun renameCharacterSheet(userId: Long, oldName: String, newName: String, newDescription: String?) {
        transaction {
            CharacterSheetsTable
                .update({ CharacterSheetsTable.userId eq userId and (CharacterSheetsTable.name eq oldName) }) {
                    it[this.name] = newName
                    newDescription?.let { newDesc -> it[this.description] = newDesc }
                    it[this.updatedAt] = LocalDateTime.now()
                }
        }
    }

    fun listCharacterSheets(userId: Long): List<Map<String, String>> =
        transaction {
            CharacterSheetsTable
                .selectAll()
                .where { CharacterSheetsTable.userId eq userId }
                .map {
                    mapOf(
                        "id" to it[CharacterSheetsTable.id].toString(),
                        "name" to it[CharacterSheetsTable.name],
                        "description" to it[CharacterSheetsTable.description]
                    )
                }
        }

    fun getDefaultCharacterSheet(userId: Long): Long? =
        transaction {
            ProfilesTable
                .selectAll()
                .where { (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq 0L) }
                .singleOrNull()
                ?.get(ProfilesTable.defaultCharacterSheetId)
        }

    fun getChosenCharacterSheet(userId: Long, groupId: Long): Long? =
        transaction {
            ProfilesTable
                .selectAll()
                .where { (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq groupId) }
                .singleOrNull()
                ?.get(ProfilesTable.selectedCharacterSheetId)
        }

    fun getActiveCharacterSheet(userId: Long, groupId: Long): Long =
        getChosenCharacterSheet(userId, groupId) ?:
        getDefaultCharacterSheet(userId) ?:
        throw CharacterSheetNoDefaultException("No default card found for $userId")

    fun getActiveCharacterSheetName(userId: Long, groupId: Long): String =
        transaction {
            CharacterSheetsTable
                .selectAll()
                .where { CharacterSheetsTable.id eq getActiveCharacterSheet(userId, groupId) }
                .singleOrNull()
            ?.get(CharacterSheetsTable.name) ?: throw CharacterSheetNotFoundException("No active character found for $userId")
        }

    fun addAttributesToCharacterSheet(userId: Long, groupId: Long, attributes: Map<String, Long>, characterSheetName: String?) {
        transaction {
            val characterSheetId =
                if (characterSheetName.isNullOrBlank())
                    getActiveCharacterSheet(userId, groupId)
                else
                    getCharacterSheetIdByName(userId, characterSheetName)

            println("characterSheetId: $characterSheetId. isNullOrBlank: ${characterSheetName.isNullOrBlank()}")

            attributes.forEach { (key, value) ->
                val existingAttribute = CharacterAttributesTable
                    .selectAll()
                    .where {
                        (CharacterAttributesTable.CharacterSheetId eq characterSheetId) and
                            (CharacterAttributesTable.attributeName eq key)
                    }
                    .singleOrNull()

                if (existingAttribute != null) {
                    CharacterAttributesTable.update({
                        (CharacterAttributesTable.CharacterSheetId eq characterSheetId) and
                            (CharacterAttributesTable.attributeName eq key)
                    }) {
                        it[successRate] = value
                        it[updatedAt] = LocalDateTime.now()
                    }
                } else {
                    CharacterAttributesTable.insert {
                        it[CharacterAttributesTable.CharacterSheetId] = characterSheetId
                        it[CharacterAttributesTable.attributeName] = key
                        it[CharacterAttributesTable.successRate] = value
                        it[CharacterAttributesTable.createdAt] = LocalDateTime.now()
                        it[CharacterAttributesTable.updatedAt] = LocalDateTime.now()
                    }
                }
            }
        }
    }

    fun getAttributesForCharacterSheet(
        userId: Long,
        groupId: Long,
        characterSheetName: String?
    ): Map<String, Long> {
        return transaction {
            val characterSheetId =
                if (characterSheetName.isNullOrBlank())
                    getActiveCharacterSheet(userId, groupId)
                else
                    getCharacterSheetIdByName(userId, characterSheetName)

            CharacterAttributesTable
                .selectAll()
                .where { CharacterAttributesTable.CharacterSheetId eq characterSheetId }
                .associate {
                    it[CharacterAttributesTable.attributeName] to it[CharacterAttributesTable.successRate]
                }
        }
    }

    fun getAttributeForCharacterSheet(userId: Long, groupId: Long, attributeName: String): Long {
        return transaction {
            val characterSheetId =
                getActiveCharacterSheet(userId, groupId)

            CharacterAttributesTable
                .selectAll()
                .where { (CharacterAttributesTable.CharacterSheetId eq characterSheetId) and (CharacterAttributesTable.attributeName eq attributeName) }
                .singleOrNull()
                ?.get(CharacterAttributesTable.successRate) ?:
                    throw CharacterSheetAttributeNotFoundException("No attribute found for $attributeName")
        }
    }

    fun deleteCharacterSheetContent(userId: Long, groupId: Long, characterSheetName: String?) {
        transaction {
            val characterSheetId =
                if (characterSheetName.isNullOrBlank())
                    getActiveCharacterSheet(userId, groupId)
                else
                    getCharacterSheetIdByName(userId, characterSheetName)

            CharacterAttributesTable.deleteWhere { CharacterAttributesTable.CharacterSheetId eq characterSheetId }
        }
    }

    fun setDefaultCharacterSheet(userId: Long, characterSheetName: String) {
        transaction {
            val now = LocalDateTime.now()

            val characterSheetId = getCharacterSheetIdByName(userId, characterSheetName)

            val existingRow = ProfilesTable
                .selectAll()
                .where { (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq 0L) }
                .singleOrNull()

            if (existingRow == null) {
                ProfilesTable.insert {
                    it[ProfilesTable.userId] = userId
                    it[ProfilesTable.groupId] = 0L
                    it[defaultCharacterSheetId] = characterSheetId
                    it[createdAt] = now
                    it[updatedAt] = now
                }
            } else {
                ProfilesTable.update(
                    where = { (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq 0L) },
                ) {
                    it[defaultCharacterSheetId] = characterSheetId
                    it[updatedAt] = now
                }
            }
        }
    }

    fun switchCharacterSheet(userId: Long, groupId: Long, characterSheetName: String) {
        transaction {
            val now = LocalDateTime.now()

            val characterSheetId = getCharacterSheetIdByName(userId, characterSheetName)

            val updatedCount = ProfilesTable
                .update({ (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq groupId) }) {
                    it[selectedCharacterSheetId] = characterSheetId
                    it[updatedAt] = now
                }

            if (updatedCount == 0) {
                ProfilesTable.insert {
                    it[this.userId] = userId
                    it[this.groupId] = groupId
                    it[this.selectedCharacterSheetId] = characterSheetId
                    it[this.createdAt] = now
                    it[this.updatedAt] = now
                }
            }
        }
    }

    private fun getCharacterSheetIdByName(userId: Long, characterSheetName: String) =
        CharacterSheetsTable
            .selectAll()
            .where { CharacterSheetsTable.userId eq userId and (CharacterSheetsTable.name eq characterSheetName) }
            .singleOrNull()?.get(CharacterSheetsTable.id) ?: throw CharacterSheetNotFoundException("$characterSheetName not found")

}
