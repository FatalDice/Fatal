package uk.akane.fatal.module.bot

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.data.VanillaStringContent.MODULE_BOT_CONTENT
import uk.akane.fatal.data.VanillaStringContent.MODULE_BOT_DESC
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.VersionUtils

class BotModule() : CommandModule {
    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher,
    ) {
        contact.sendMessage(
            dispatcher.translator.getTranslation(
                VanillaStringContent.StringTypes.BOT_MESSAGE,
                this
            )
        )
    }

    override val commandPrefix: String = "bot"

    override fun generateKeywordReplacements() = mapOf(
        "PluginVersionHeader" to VersionUtils.getPluginVersionHeader(),
        "CompilationTime" to VersionUtils.getCompilationTime(),
        "JdkVersion" to VersionUtils.getJdkVersion(),
        "OSName" to VersionUtils.getOSName(),
        "OpenSourceAddress" to VersionUtils.getOpenSourceAddress()
    )

    override val helpDescription = MODULE_BOT_DESC
    override val helpContent = MODULE_BOT_CONTENT
}

