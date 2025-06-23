package uk.akane.fatal

import net.mamoe.mirai.message.data.Message
import org.jetbrains.annotations.TestOnly

@TestOnly
class ConsoleMessage(private val constructString: String) : Message {
    override fun contentToString(): String = constructString

    override fun toString(): String = constructString
}