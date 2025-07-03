package uk.akane.fatal.module.character

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.data.database.profile.character.CharacterDao
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.CharacterCardNotFoundException

class CharacterCardModule : CommandModule {
    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        val (operation, param1, param2, param3) = parseParameters(parameter)
        try {
            when (operation) {
                "default" -> {
                    CharacterDao.setDefaultCharacterCard(sender.id, param1)
                }
                "switch" -> {
                    CharacterDao.switchCharacterCard(
                        sender.id,
                        if (contact is Group) contact.id else 0L,
                        param1
                    )
                }
                "create" -> {

                }
                "delete" -> {

                }
                "rename" -> {

                }
                "list" -> {

                }
            }
        } catch (e: CharacterCardNotFoundException) {

        }
    }

    private fun parseParameters(parameter: String): List<String> {
        val parts = parameter.split(' ')

        val operation = parts.getOrElse(0) { "" }
        val param1 = parts.getOrElse(1) { "" }
        val param2 = parts.getOrElse(2) { "" }
        val param3 = parts.getOrElse(3) { "" }

        return listOf(operation, param1, param2, param3)
    }

    override fun generateKeywordReplacements() = mapOf(
        "" to ""
    )

    override val helpDescription =
        VanillaStringContent.MODULE_CHARACTER_CARD_DESC
    override val helpContent =
        VanillaStringContent.MODULE_CHARACTER_CARD_CONTENT
    override val commandPrefix =
        "cc"
}