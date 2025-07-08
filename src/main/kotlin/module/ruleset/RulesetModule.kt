package uk.akane.fatal.module.ruleset

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.data.database.universal.RulesetsTableDao
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.DiceUtils.evaluateExpressionRaw
import uk.akane.fatal.utils.parseParameters

open class RulesetModule : CommandModule {

    private var dispatcher: Dispatcher? = null
    private var contact: Contact? = null
    private var rulesetList: List<String>? = null
    var compiledList: String = ""

    private var operation: String = ""
    private var rulesetName: String = ""
    private var rulesetValue: String = ""

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        this.dispatcher = dispatcher
        this.contact = contact
        compiledList = ""

        operation = parameter.substringBefore(' ').trim()
        val rulesetNameRaw = parameter.substringAfter(' ')
        val rulesetValueRaw = rulesetNameRaw.substringAfter(' ')

        rulesetName = getRulesetName().ifBlank { rulesetNameRaw.substringBefore(' ') }.trim()
        rulesetValue = rulesetValueRaw.substringBefore(' ').trim()

        try {
            val resultStringType =
                when (OperationType.valueOf(getOperation().ifBlank { operation })) {
                    OperationType.ADD -> {
                        RulesetsTableDao.insert(
                            rulesetName,
                            rulesetValue.compileToList()
                        )
                        VanillaStringContent.StringTypes.RULESET_INSERT_COMPLETE
                    }

                    OperationType.REMOVE -> {
                        RulesetsTableDao.deleteById(
                            rulesetName
                        )
                        VanillaStringContent.StringTypes.RULESET_DELETE_COMPLETE
                    }

                    OperationType.LIST -> {
                        rulesetList = RulesetsTableDao.listAllRulesetIds()
                        VanillaStringContent.StringTypes.RULESET_LIST
                    }

                    OperationType.GENERATE -> {
                        val entries = getRulesetEntries().ifEmpty {
                            RulesetsTableDao.queryById(rulesetName)
                        }

                        val times = rulesetValue.toIntOrNull() ?: 1
                        repeat(times) { index ->
                            if (getOperation().isBlank()) {
                                generateRuleset(entries, contact)
                                if (times > 1 && index != times - 1) compiledList += "\n\n"
                            } else {
                                generateRulesetFormula(entries, contact, times, index)
                            }
                        }

                        if (compiledList.isBlank()) throw IllegalArgumentException("Compiled list is empty")

                        VanillaStringContent.StringTypes.RULESET_GENERATION
                    }
                }

            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    resultStringType,
                    this@RulesetModule,
                )
            )
        } catch (e: IllegalArgumentException) {
            announceIllegalOperationType()
            e.printStackTrace()
        } catch (e: IndexOutOfBoundsException) {
            announceIllegalRulesetValue()
            onErrorRemoveUnfinishedRuleset(rulesetName)
            e.printStackTrace()
        }
    }

    private fun generateRuleset(entries: List<Pair<String, String>>, contact: Contact) {
        val maxKeyLength = entries.maxOfOrNull { it.first.length.coerceAtMost(12) } ?: 8

        val (rendered, total, special) = entries.fold(Triple(mutableListOf<String>(), 0, 0)) { (list, sum, specialSum), (key, value) ->
            val result = evaluateExpressionRaw(value, contact).first.first.toInt()
            val newSum = sum + result
            val newSpecial = if (key.endsWith('*')) specialSum + result else specialSum
            list += key.removeSuffix("*").padEnd(maxKeyLength) + "  " + result
            Triple(list, newSum, newSpecial)
        }

        compiledList += rendered.chunked(3).joinToString("\n") { it.joinToString("    ") }
        compiledList += "\n[${total - special}/$total]"
    }

    open fun getOperation() = ""

    open fun getRulesetName() = ""

    open fun getRulesetValue() = ""

    open fun getRulesetEntries(): List<Pair<String, String>> = emptyList()

    open fun generateRulesetFormula(
        entries: List<Pair<String, String>>,
        contact: Contact,
        times: Int,
        index: Int
    ) {
        generateRuleset(entries, contact)
        if (times > 1 && index != times - 1) {
            compiledList += "\n\n"
        }
    }

    override fun generateKeywordReplacements() = mapOf(
        "RulesetName" to rulesetName,
        "RulesetList" to (rulesetList?.joinToString("\n") ?: ""),
        "RulesetGeneration" to compiledList
    )

    private suspend fun announceIllegalOperationType() = contact!!.sendMessage(
        dispatcher!!.translator.getTranslation(
            VanillaStringContent.StringTypes.RULESET_ILLEGAL_OPERATION,
            this
        )
    )

    private suspend fun announceIllegalRulesetValue() = contact!!.sendMessage(
        dispatcher!!.translator.getTranslation(
            VanillaStringContent.StringTypes.RULESET_ILLEGAL_RULESET_VALUE,
            this
        )
    )

    private fun String.compileToList() =
        this
            .split(',')
            .map {
                val (key, value) = it.split('=', limit = 2)
                key.trim() to value.trim()
            }

    private fun onErrorRemoveUnfinishedRuleset(rulesetName: String) {
        RulesetsTableDao.deleteById(rulesetName)
    }

    override val helpDescription =
        VanillaStringContent.MODULE_RULESET_DESC
    override val helpContent =
        VanillaStringContent.MODULE_RULESET_CONTENT
    override val commandPrefix: String =
        "ruleset"

    enum class OperationType() {
        ADD,
        REMOVE,
        LIST,
        GENERATE
    }

}