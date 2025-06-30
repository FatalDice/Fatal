package uk.akane.fatal.module.ruleset.alias

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.module.ruleset.RulesetModule

class COCModule : RulesetModule() {

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
        "coc"

    override fun getRulesetValue() =
        generateCount.toString()

    override fun getRulesetEntries(): List<Pair<String, String>> = listOf(
        "力量" to "3d6*5",
        "体质" to "3d6*5",
        "体型" to "(2d6+6)*5",
        "敏捷" to "3d6*5",
        "外貌" to "3d6*5",
        "智力" to "(2d6+6)*5",
        "意志" to "3d6*5",
        "教育" to "(2d6+6)*5",
        "幸运" to "3d6*5",
    )

    override val helpDescription =
        VanillaStringContent.MODULE_COC_DESC
    override val helpContent =
        VanillaStringContent.MODULE_COC_CONTENT
    override val commandPrefix =
        "coc"
}