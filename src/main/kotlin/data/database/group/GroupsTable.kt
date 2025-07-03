package uk.akane.fatal.data.database.group

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object GroupsTable : Table() {
    val groupId = long("group_id")

    val faceCount = long("face_count")

    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    override val primaryKey = PrimaryKey(groupId)
}