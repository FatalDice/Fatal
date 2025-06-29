package uk.akane.fatal.data.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object ProfilesTable : Table() {
    val userId = long("userid")
    val groupId = long("group_id")

    val nickName = varchar("nickname", 64).nullable()
    val faceCount = long("face_count").nullable()

    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(userId, groupId)
}