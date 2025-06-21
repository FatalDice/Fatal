package uk.akane.fatal.module

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.utils.VersionUtils

class HelpModule : CommandModule {
    override suspend fun invoke(
        event: Event,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        if (parameter.isBlank()) {
            contact.sendMessage(
                String.format(
                    dispatcher.getTranslator().getTemplate(VanillaStringContent.HELP_MAIN_PAGE),
                    VersionUtils.getPluginVersionHeader(),
                    VanillaStringContent.HELP_WELCOME_BANNER)
            )
            return
        }
    }

    override val commandPrefix: String
        get() = "help"
}