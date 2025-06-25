package uk.akane.fatal.module.help

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.VersionUtils

class HelpModule: CommandModule {
    private var dispatcher: Dispatcher? = null

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        this.dispatcher = dispatcher

        if (parameter.isBlank()) {
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.HELP_MAIN_PAGE,
                    this
                )
            )
            return
        }
    }

    override val commandPrefix: String
        get() = "help"

    override fun generateKeywordReplacements() = mapOf(
            "PluginVersionHeader" to VersionUtils.getPluginVersionHeader(),
            "HelpWelcomeBanner" to (dispatcher?.translator?.getTranslation(VanillaStringContent.StringTypes.HELP_WELCOME_BANNER) ?: ""),
        )
}