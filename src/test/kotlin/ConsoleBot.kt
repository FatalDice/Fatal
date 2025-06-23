package uk.akane.fatal

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.ContactList
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.OtherClient
import net.mamoe.mirai.contact.Stranger
import net.mamoe.mirai.contact.friendgroup.FriendGroups
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.MiraiLogger
import org.jetbrains.annotations.TestOnly
import kotlin.coroutines.CoroutineContext

@TestOnly
class ConsoleBot : Bot {
    override val asFriend: Friend
        get() = TODO("Not yet implemented")
    override val asStranger: Stranger
        get() = TODO("Not yet implemented")
    override val configuration: BotConfiguration
        get() = TODO("Not yet implemented")
    override val eventChannel: EventChannel<BotEvent>
        get() = TODO("Not yet implemented")
    override val friendGroups: FriendGroups
        get() = TODO("Not yet implemented")
    override val friends: ContactList<Friend>
        get() = TODO("Not yet implemented")
    override val groups: ContactList<Group>
        get() = TODO("Not yet implemented")
    override val isOnline: Boolean
        get() = TODO("Not yet implemented")
    override val logger: MiraiLogger
        get() = TODO("Not yet implemented")
    override val otherClients: ContactList<OtherClient>
        get() = TODO("Not yet implemented")
    override val strangers: ContactList<Stranger>
        get() = TODO("Not yet implemented")

    override fun close(cause: Throwable?) {
        TODO("Not yet implemented")
    }

    override suspend fun login() {
        TODO("Not yet implemented")
    }

    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")
    override val id: Long
        get() = TODO("Not yet implemented")
    override val nick: String
        get() = TODO("Not yet implemented")
}