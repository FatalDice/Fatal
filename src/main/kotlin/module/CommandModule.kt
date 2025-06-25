package uk.akane.fatal.module

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher

interface CommandModule {
    suspend fun invoke(event: Event, sender: Contact, contact: Contact, parameter: String, dispatcher: Dispatcher)
    suspend fun initialize(dispatcher: Dispatcher) { return }
    suspend fun reset(dispatcher: Dispatcher) { return }
    suspend fun destroy() { return }
    fun isMultipleInvoke(): Boolean = false
    fun generateKeywordReplacements(): Map<String, String>

    val helpDescription: String
    val helpContent: String
    val commandPrefix: String
}