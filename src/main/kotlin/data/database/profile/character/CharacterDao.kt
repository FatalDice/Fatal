package uk.akane.fatal.data.database.profile.character

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import uk.akane.fatal.data.database.profile.ProfilesIndependentTable
import uk.akane.fatal.data.database.profile.ProfilesIndependentTable.defaultCharacterCardId
import uk.akane.fatal.data.database.profile.ProfilesTable
import uk.akane.fatal.utils.CharacterCardNotFoundException
import java.time.LocalDateTime

object CharacterDao {

    fun setDefaultCharacterCard(userId: Long, characterCardName: String) {
        transaction {
            val characterCardId = getCharacterCardIdByName(userId, characterCardName)
                ?: throw CharacterCardNotFoundException("$characterCardName not found")

            ProfilesIndependentTable
                .update({ ProfilesIndependentTable.userId eq userId }) {
                    it[defaultCharacterCardId] = characterCardId
                }
        }
    }

    fun switchCharacterCard(userId: Long, groupId: Long, characterCardName: String) {
        transaction {
            val now = LocalDateTime.now()

            val characterCardId = getCharacterCardIdByName(userId, characterCardName)
                ?: throw CharacterCardNotFoundException("$characterCardName not found")

            val updatedCount = ProfilesTable
                .update({ (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq groupId) }) {
                    it[selectedCharacterCardId] = characterCardId
                    it[updatedAt] = now
                }

            if (updatedCount == 0) {
                ProfilesTable.insert {
                    it[this.userId] = userId
                    it[this.groupId] = groupId
                    it[this.selectedCharacterCardId] = characterCardId
                    it[this.createdAt] = now
                    it[this.updatedAt] = now
                }
            }
        }
    }

    private fun getCharacterCardIdByName(userId: Long, characterCardName: String) =
        CharacterCardsTable
            .selectAll()
            .where { CharacterCardsTable.userId eq userId and (CharacterCardsTable.name eq characterCardName) }
            .singleOrNull()?.get(CharacterCardsTable.id)

}
