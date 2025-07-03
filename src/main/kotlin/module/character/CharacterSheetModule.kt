package uk.akane.fatal.module.character

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.data.database.profile.character.CharacterDao
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.CharacterSheetNotFoundException
import uk.akane.fatal.utils.MessageUtils

class CharacterSheetModule : CommandModule {

    private var characterSheetName = ""
    private var characterSheetList = ""
    private var characterSheetNewName = ""
    private lateinit var sender: Contact
    private lateinit var context: Contact

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        val (operation, param1, param2, param3) = parseParameters(parameter)
        var reply = ""
        characterSheetName = param1
        characterSheetNewName = param2
        this.sender = sender
        this.context = contact

        try {
            when (operation) {
                "default" -> {
                    CharacterDao.setDefaultCharacterSheet(sender.id, param1)
                    reply = dispatcher.translator.getTranslation(
                        VanillaStringContent.StringTypes.CHARACTER_SHEET_SET_DEFAULT,
                        this
                    )
                }
                "switch" -> {
                    CharacterDao.switchCharacterSheet(
                        sender.id,
                        if (contact is Group) contact.id else 0L,
                        param1
                    )
                    reply = dispatcher.translator.getTranslation(
                        VanillaStringContent.StringTypes.CHARACTER_SHEET_SET_SWITCH,
                        this
                    )
                }
                "create" -> {
                    CharacterDao.createCharacterSheet(
                        sender.id,
                        param1,
                        param2
                    )
                    reply = dispatcher.translator.getTranslation(
                        VanillaStringContent.StringTypes.CHARACTER_SHEET_CREATE_SUCCESSFUL,
                        this
                    )
                }
                "delete" -> {
                    CharacterDao.deleteCharacterSheet(
                        sender.id,
                        param1
                    )
                    reply = dispatcher.translator.getTranslation(
                        VanillaStringContent.StringTypes.CHARACTER_SHEET_DELETE_SUCCESSFUL,
                        this
                    )
                }
                "rename" -> {
                    CharacterDao.renameCharacterSheet(
                        sender.id,
                        param1,
                        param2,
                        param3.ifBlank { null }
                    )
                    reply = dispatcher.translator.getTranslation(
                        VanillaStringContent.StringTypes.CHARACTER_SHEET_RENAME_SUCCESSFUL,
                        this
                    )
                }
                "list" -> {
                    val map = CharacterDao.listCharacterSheets(sender.id)
                    val defaultCharacterSheetId = CharacterDao.getDefaultCharacterSheet(sender.id)
                    val selectedCharacterSheetId = CharacterDao.getChosenCharacterSheet(
                        sender.id,
                        if (contact is Group) contact.id else 0L
                    )
                    characterSheetList = map.joinToString("\n") {
                        "· ${it["name"]} " +
                        (if (defaultCharacterSheetId != null && it["id"] == defaultCharacterSheetId.toString()) "#" else "") +
                        (if (selectedCharacterSheetId != null && it["id"] == selectedCharacterSheetId.toString()) "*" else "") +
                        "\n" +
                        " - ${it["description"]}"
                    }
                    reply = dispatcher.translator.getTranslation(
                        VanillaStringContent.StringTypes.CHARACTER_SHEET_LIST_GENERAL,
                        this
                    )
                }
            }
            contact.sendMessage(reply)
        } catch (e: CharacterSheetNotFoundException) {
            e.printStackTrace()
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.CHARACTER_SHEET_NOT_FOUND,
                    this
                )
            )
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
        "CharacterSheetName" to characterSheetName,
        "CharacterSheetList" to characterSheetList,
        "SenderName" to MessageUtils.getSenderName(sender, context),
        "CharacterSheetNewName" to characterSheetNewName
    )

    override val helpDescription =
        VanillaStringContent.MODULE_CHARACTER_SHEET_DESC
    override val helpContent =
        VanillaStringContent.MODULE_CHARACTER_SHEET_CONTENT
    override val commandPrefix =
        "cs"
}