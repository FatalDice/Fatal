package uk.akane.fatal.utils

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Member

object MessageUtils {
    fun getSenderName(sender: Contact) =
        if (sender is Friend)
            sender.nick
        else if (sender is Member && sender.nameCard.isNotBlank())
            sender.nameCard
        else if (sender is Member)
            sender.nick
        else
            ""
}