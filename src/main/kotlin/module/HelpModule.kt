package uk.akane.fatal.module

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent.StringTypes
import uk.akane.fatal.utils.VersionUtils

class HelpModule: CommandModule {
    private var dispatcher: Dispatcher? = null

    override suspend fun invoke(
        event: Event,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        this.dispatcher = dispatcher

        if (parameter.isBlank()) {
            contact.sendMessage(
                dispatcher.getTranslator().getTranslation(
                    StringTypes.HELP_MAIN_PAGE,
                    this
                )
            )
            return
        }
    }

    override val commandPrefix: String
        get() = "help"

    override val keywordReplacements: Map<String, String>
        get() = mapOf(
            "PluginVersionHeader" to VersionUtils.getPluginVersionHeader(),
            "HelpWelcomeBanner" to dispatcher?.getTranslator()!!.getTranslation(StringTypes.HELP_WELCOME_BANNER),
        )
}