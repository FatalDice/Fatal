package uk.akane.fatal.data.database

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

object ProfilesTableDao {
    // Nickname
    fun setNickname(userId: Long, groupId: Long, nickname: String) {
        transaction {
            val now = LocalDateTime.now()

            val existingRow = ProfilesTable
                .selectAll()
                .where { (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq groupId) }
                .singleOrNull()

            if (existingRow == null) {
                ProfilesTable.insert {
                    it[ProfilesTable.userId] = userId
                    it[ProfilesTable.groupId] = groupId
                    it[ProfilesTable.nickName] = nickname
                    it[createdAt] = now
                    it[updatedAt] = now
                }
            } else {
                ProfilesTable.update(
                    where = { (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq groupId) }
                ) {
                    it[nickName] = nickname
                    it[updatedAt] = now
                }
            }
        }
    }

    fun getNickname(userId: Long, groupId: Long): String? {
        return transaction {
            ProfilesTable
                .selectAll()
                .where { (ProfilesTable.userId eq userId) and (ProfilesTable.groupId eq groupId) }
                .limit(1)
                .map { it[ProfilesTable.nickName] }
                .singleOrNull()
        }
    }

}