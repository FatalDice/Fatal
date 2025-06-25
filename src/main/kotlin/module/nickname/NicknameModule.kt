package uk.akane.fatal.module.nickname

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.data.database.DatabaseFactory
import uk.akane.fatal.data.database.ProfilesTable
import uk.akane.fatal.data.database.ProfilesTableDao
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.MessageUtils

class NicknameModule : CommandModule {
    private lateinit var sender: Contact
    private var lastParameter = ""

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        this.sender = sender
        this.lastParameter = parameter

        val isSetNickname = parameter.isNotBlank()
        contact.sendMessage(
            dispatcher.translator.getTranslation(
                if (isSetNickname)
                    VanillaStringContent.StringTypes.NICKNAME_SET
                else
                    VanillaStringContent.StringTypes.NICKNAME_UNSET,
                this
            )
        )
        if (isSetNickname) {
            ProfilesTableDao.setNickname(
                sender.id,
                if (contact is Group) contact.id else 0,
                parameter
            )
        } else {
            ProfilesTableDao.setNickname(
                sender.id,
                if (contact is Group) contact.id else 0,
                ""
            )
        }
    }

    override fun generateKeywordReplacements() = mapOf(
        "SenderName" to MessageUtils.getRawSenderName(sender),
        "NickName" to lastParameter
    )

    override val helpDescription: String =
        VanillaStringContent.MODULE_NICKNAME_DESC
    override val helpContent: String =
        VanillaStringContent.MODULE_NICKNAME_CONTENT
    override val commandPrefix: String =
        "nn"
}