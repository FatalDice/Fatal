package uk.akane.fatal

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.ShortVideo
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource
import org.jetbrains.annotations.TestOnly
import kotlin.coroutines.CoroutineContext

@TestOnly
class ConsoleContact : Contact {
    override val bot: Bot
        get() = ConsoleBot()
    override val id: Long
        get() = 0

    override suspend fun sendMessage(message: Message): MessageReceipt<Contact> {
        println(message.content)
        throw UnsupportedOperationException("sendMessage is partially implemented for testing.")
    }

    override suspend fun uploadImage(resource: ExternalResource): Image {
        return Image("0")
    }

    override suspend fun uploadShortVideo(
        thumbnail: ExternalResource,
        video: ExternalResource,
        fileName: String?
    ): ShortVideo {
        TODO("Not yet implemented")
    }

    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")
}