package uk.akane.fatal.data.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object RulesetsTable : Table() {
    val rulesetId = varchar("ruleset_id", 64)
        .references(RulesetsIndexTable.id, onDelete = ReferenceOption.CASCADE)
    val key = varchar("key", 64)
    val value = varchar("value", 64)

    override val primaryKey: PrimaryKey = PrimaryKey(rulesetId, key)
}