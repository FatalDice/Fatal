package uk.akane.fatal.module

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent.StringTypes
import uk.akane.fatal.utils.VersionUtils

class BotModule() : CommandModule {
    override suspend fun invoke(
        event: Event,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        contact.sendMessage(
            dispatcher.getTranslator().getTranslation(
                StringTypes.BOT_MESSAGE,
                this
            )
        )
    }

    override val commandPrefix: String = "bot"

    override val keywordReplacements: Map<String, String> = mapOf(
        "PluginVersionHeader" to VersionUtils.getPluginVersionHeader(),
        "CompilationTime" to  VersionUtils.getCompilationTime(),
        "JdkVersion" to VersionUtils.getJdkVersion(),
        "OSName" to VersionUtils.getOSName(),
        "OpenSourceAddress" to VersionUtils.getOpenSourceAddress()
    )
}