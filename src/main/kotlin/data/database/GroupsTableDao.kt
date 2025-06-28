package uk.akane.fatal.data.database

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import uk.akane.fatal.utils.DiceUtils
import java.time.LocalDateTime

object GroupsTableDao {
    fun setDefaultDice(groupId: Long, counts: Long) {
        transaction {
            val now = LocalDateTime.now()

            val existingRow = GroupsTable
                .selectAll()
                .where { GroupsTable.groupId eq groupId }
                .singleOrNull()

            if (existingRow == null) {
                GroupsTable.insert {
                    it[GroupsTable.groupId] = groupId
                    it[faceCount] = counts
                    it[createdAt] = now
                    it[updatedAt] = now
                }
            } else {
                GroupsTable.update(
                    where = { GroupsTable.groupId eq groupId },
                ) {
                    it[faceCount] = counts
                    it[updatedAt] = now
                }
            }
        }
    }

    fun getDiceCount(groupId: Long): Long =
        transaction {
            GroupsTable
                .selectAll()
                .where { GroupsTable.groupId eq groupId }
                .singleOrNull()
                ?.let { it[GroupsTable.faceCount] }
                ?: DiceUtils.DEFAULT_DICE
        }

}