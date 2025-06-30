package uk.akane.fatal.module.ruleset.alias

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.module.ruleset.RulesetModule
import uk.akane.fatal.utils.DiceUtils.evaluateExpressionRaw

class DNDModule : RulesetModule() {

    private var generateCount = 1

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        generateCount = parameter.toIntOrNull() ?: 1
        super.invoke(event, sender, contact, parameter, dispatcher)
    }

    override fun getOperation() =
        "generate"

    override fun getRulesetName() =
        "dnd"

    override fun getRulesetValue() =
        generateCount.toString()

    override fun getRulesetEntries(): List<Pair<String, String>> = listOf(
        "v1" to "4d6kh3",
        "v2" to "4d6kh3",
        "v3" to "4d6kh3",
        "v4" to "4d6kh3",
        "v5" to "4d6kh3",
        "v6" to "4d6kh3",
    )

    override fun generateRulesetFormula(
        entries: List<Pair<String, String>>,
        contact: Contact,
        times: Int,
        index: Int
    ) {
        val sortedResults = entries.map { (_, value) ->
            evaluateExpressionRaw(value, contact).first.first
        }.sortedDescending()

        compiledList += "[" + sortedResults.joinToString(", ") + "]" +
            ": " + sortedResults.sum()

        if (times > 1 && index != times - 1) {
            compiledList += "\n"
        }
    }

    override val helpDescription =
        VanillaStringContent.MODULE_DND_DESC
    override val helpContent =
        VanillaStringContent.MODULE_DND_CONTENT
    override val commandPrefix =
        "dnd"
}