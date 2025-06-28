package uk.akane.fatal.module.set.alias

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.module.set.SetModule

class DefaultDiceModule : SetModule() {
    private var parameter: Long? = null

    override fun generateKeywordReplacements() =
        mapOf(
            "DefaultDice" to parameter.toString()
        )

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        this.parameter = parameter.toLongOrNull()
        super.invoke(event, sender, contact, parameter, dispatcher)
    }

    override fun getSetSuccessfulString() =
        VanillaStringContent.StringTypes.DEFAULT_DICE_SET

    override fun getUnsetSuccessfulString() =
        VanillaStringContent.StringTypes.DEFAULT_DICE_UNSET

    override fun getSetIllegalKeyString() =
        VanillaStringContent.StringTypes.DEFAULT_DICE_ILLEGAL

    override fun getVariableName() =
        "defaultdice"

    override val helpDescription =
        VanillaStringContent.MODULE_DEFAULT_DICE_DESC
    override val helpContent =
        VanillaStringContent.MODULE_DEFAULT_DICE_CONTENT
    override val commandPrefix: String =
        "dd"
}