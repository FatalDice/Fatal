package uk.akane.fatal.data.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object RulesetsTableDao {

    fun insert(rulesetName: String, rulesetList: List<Pair<String, String>>) {
        transaction {
            RulesetsIndexTable.insertIgnore {
                it[RulesetsIndexTable.id] = rulesetName
            }

            rulesetList.forEach { (k, v) ->
                RulesetsTable.insertIgnore {
                    it[rulesetId] = rulesetName
                    it[key] = k
                    it[value] = v
                }
            }
        }
    }

    fun listAllRulesetIds(): List<String> = transaction {
        RulesetsIndexTable
            .selectAll()
            .map { it[RulesetsIndexTable.id] }
    }

    fun queryById(id: String): List<Pair<String, String>> = transaction {
        RulesetsTable
            .selectAll()
            .where { RulesetsTable.rulesetId eq id }
            .map { it[RulesetsTable.key] to it[RulesetsTable.value] }
    }

    fun deleteById(id: String) {
        transaction {
            RulesetsIndexTable.deleteWhere { RulesetsIndexTable.id eq id }
        }
    }
}
