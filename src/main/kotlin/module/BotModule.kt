package uk.akane.fatal.module

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.utils.VersionUtils

class BotModule : CommandModule {
    override suspend fun invoke(
        event: Event,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        contact.sendMessage(
            String.format(
                dispatcher.getTranslator().getTemplate(VanillaStringContent.StringTypes.BOT_MESSAGE),
                VersionUtils.getPluginVersionHeader(),
                VersionUtils.getCompilationTime(),
                VersionUtils.getJdkVersion(),
                VersionUtils.getOSName(),
                VersionUtils.getOpenSourceAddress()
            )
        )
    }

    override val commandPrefix: String = "bot"
}