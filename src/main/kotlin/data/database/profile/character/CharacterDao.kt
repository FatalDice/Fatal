package uk.akane.fatal.data.database.profile.character

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import uk.akane.fatal.data.database.profile.ProfilesTable
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

            if (characterSheetId != null) {
                // First, check if it's the default character card for the user
                val defaultCharacterSheetId = ProfilesTable
                    .selectAll()
                    .where { (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq 0L) }
                    .singleOrNull()?.get(ProfilesTable.defaultCharacterSheetId)

                if (defaultCharacterSheetId == characterSheetId) {
                    ProfilesTable.update({ (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq 0L) }) {
                        it[ProfilesTable.defaultCharacterSheetId] = null
                    }
                }

                CharacterSheetsTable.deleteWhere { CharacterSheetsTable.id eq characterSheetId }
            } else {
                throw CharacterSheetNotFoundException("$characterCardName not found")
            }
        }
    }

    fun renameCharacterSheet(userId: Long, oldName: String, newName: String, newDescription: String?) {
        transaction {
            if (getCharacterSheetIdByName(userId, oldName) == null) throw CharacterSheetNotFoundException("$oldName not found")
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

    fun setDefaultCharacterSheet(userId: Long, characterSheetName: String) {
        transaction {
            val now = LocalDateTime.now()

            val characterSheetId = getCharacterSheetIdByName(userId, characterSheetName)
                ?: throw CharacterSheetNotFoundException("$characterSheetName not found")

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
                ?: throw CharacterSheetNotFoundException("$characterSheetName not found")

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
            .singleOrNull()?.get(CharacterSheetsTable.id)

}
