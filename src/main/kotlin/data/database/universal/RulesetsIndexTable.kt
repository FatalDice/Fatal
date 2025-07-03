package uk.akane.fatal.data.database.universal

import org.jetbrains.exposed.sql.Table

object RulesetsIndexTable : Table() {
    val id = varchar("id", 64)
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}