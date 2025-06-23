package uk.akane.fatal

import net.mamoe.mirai.event.Event
import org.jetbrains.annotations.TestOnly

@TestOnly
class ConsoleInputEvent : Event {
    override val isIntercepted: Boolean
        get() = false

    override fun intercept() {
        // TODO
    }
}