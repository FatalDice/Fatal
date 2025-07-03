package uk.akane.fatal.utils

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import uk.akane.fatal.data.database.profile.ProfilesTableDao

object MessageUtils {
    fun getRawSenderName(sender: Contact) =
        if (sender is Friend)
            sender.nick
        else if (sender is Member && sender.nameCard.isNotBlank())
            sender.nameCard
        else if (sender is Member)
            sender.nick
        else
            ""

    fun getSenderName(sender: Contact, context: Contact): String {
        try {
            var databaseName = getNickName(sender, context)
            if (databaseName?.isBlank() == true) databaseName = null
            return databaseName ?: getRawSenderName(sender)
        } catch (_: Exception) {
            return ""
        }
    }

    fun setNickName(sender: Contact, context: Contact, name: String) {
        ProfilesTableDao.setNickname(
            sender.id,
            if (context is Group) context.id else 0,
            name
        )
    }

    fun getNickName(sender: Contact, context: Contact) =
        ProfilesTableDao.getNickname(
            sender.id,
            if (context is Group) context.id else 0
        )
}