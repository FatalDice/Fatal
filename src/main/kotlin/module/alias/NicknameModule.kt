package uk.akane.fatal.module.alias

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.module.set.SetModule
import uk.akane.fatal.utils.MessageUtils

class NicknameModule : SetModule() {
    private lateinit var sender: Contact
    private var lastParameter = ""

    override fun generateKeywordReplacements() = mapOf(
        "SenderName" to MessageUtils.getRawSenderName(sender),
        "NickName" to lastParameter
    )

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        this.sender = sender
        this.lastParameter = parameter
        super.invoke(event, sender, contact, parameter, dispatcher)
    }

    override fun getSetSuccessfulString(): VanillaStringContent.StringTypes =
        VanillaStringContent.StringTypes.NICKNAME_SET

    override fun getUnsetSuccessfulString(): VanillaStringContent.StringTypes =
        VanillaStringContent.StringTypes.NICKNAME_UNSET

    override fun getVariableName() =
        "nickname"

    override val helpDescription: String =
        VanillaStringContent.MODULE_NICKNAME_DESC
    override val helpContent: String =
        VanillaStringContent.MODULE_NICKNAME_CONTENT
    override val commandPrefix: String =
        "nn"
}