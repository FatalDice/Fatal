package uk.akane.fatal.module

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher

interface CommandModule {
    suspend fun invoke(event: Event, contact: Contact, parameter: String, dispatcher: Dispatcher)
    fun isMultipleInvoke(): Boolean = false

    suspend fun initialize(dispatcher: Dispatcher) { return }
    suspend fun reset(dispatcher: Dispatcher) { return }
    suspend fun destroy() { return }

    val commandPrefix: String
}