package uk.akane.fatal.module.character

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.module.CommandModule

class RollAttributeModule : CommandModule {
    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        TODO("Not yet implemented")
    }

    override fun generateKeywordReplacements(): Map<String, String> {
        TODO("Not yet implemented")
    }

    override val helpDescription: String
        get() = TODO("Not yet implemented")
    override val helpContent: String
        get() = TODO("Not yet implemented")
    override val commandPrefix: String
        get() = TODO("Not yet implemented")
}