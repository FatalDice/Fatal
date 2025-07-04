package uk.akane.fatal.module.character

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.data.database.profile.character.CharacterDao
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.CharacterSheetAttributeNotFoundException
import uk.akane.fatal.utils.CharacterSheetNoDefaultException
import uk.akane.fatal.utils.CharacterSheetNotFoundException
import uk.akane.fatal.utils.getGroupIdOrZero
import uk.akane.fatal.utils.parseParameters

class SetAttributeModule : CommandModule {

    private var lastCardName = ""
    private var operation = ""
    private var attributeMap = mapOf<String, Long>()

    private var attributeValue = ""

    private lateinit var sender: Contact
    private lateinit var context: Contact

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        this.sender = sender
        this.context = contact

        val (operation, targetCardName) = parameter.parseParameters()
        var reply: String
        lastCardName = targetCardName
        this.operation = operation

        try {
            if (operation == "clear") {
                CharacterDao.deleteCharacterSheetContent(
                    sender.id,
                    contact.getGroupIdOrZero(),
                    targetCardName
                )
                reply = dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_ATTRIBUTE_DELETE_SUCCESSFUL,
                    this
                )
            } else if (operation == "show" && lastCardName.isNotBlank()) {
                attributeValue = CharacterDao.getAttributeForCharacterSheet(
                    sender.id,
                    contact.getGroupIdOrZero(),
                    lastCardName
                ).toString()
                reply = dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_ATTRIBUTE_ATTRIBUTE_DISPLAY,
                    this
                )
            } else if (operation == "show") {
                attributeMap = CharacterDao.getAttributesForCharacterSheet(
                    sender.id,
                    contact.getGroupIdOrZero(),
                    null
                )
                reply = dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_ATTRIBUTE_ATTRIBUTE_MAP_DISPLAY,
                    this
                )
            } else {
                attributeMap = operation.parseAttributes()
                CharacterDao.addAttributesToCharacterSheet(
                    sender.id,
                    contact.getGroupIdOrZero(),
                    attributeMap,
                    targetCardName
                )
                reply = dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_ATTRIBUTE_INSERT_SUCCESSFUL,
                    this
                )
            }
            contact.sendMessage(reply)
        } catch (_: CharacterSheetNoDefaultException) {
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_ATTRIBUTE_ACTIVE_CHARACTER_SHEET_NOT_FOUND,
                    this,
                    false
                )
            )
            CharacterDao.createCharacterSheet(
                sender.id,
                DEFAULT_CHARACTER_SHEET_NAME,
                DEFAULT_CHARACTER_SHEET_DESCRIPTION
            )
            CharacterDao.setDefaultCharacterSheet(
                sender.id,
                DEFAULT_CHARACTER_SHEET_NAME
            )
        } catch (_: CharacterSheetNotFoundException) {
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_ATTRIBUTE_CLEAR_CHARACTER_SHEET_NOT_FOUND,
                    this
                )
            )
        } catch (_: CharacterSheetAttributeNotFoundException) {
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_ATTRIBUTE_ATTRIBUTE_NOT_FOUND,
                    this
                )
            )
        }
    }

    fun String.parseAttributes(): Map<String, Long> {
        val attributes = mutableMapOf<String, Long>()
        var attributeName = StringBuilder()
        var attributeValue = StringBuilder()

        var isParsingName = true
        for (char in this) {
            when {
                char.isDigit() -> {
                    if (isParsingName) {
                        isParsingName = false
                    }
                    attributeValue.append(char)
                }

                else -> {
                    if (!isParsingName) {
                        attributes[attributeName.toString()] = attributeValue.toString().toLong()
                        attributeName = StringBuilder()
                        attributeValue = StringBuilder()
                        isParsingName = true
                    }
                    attributeName.append(char)
                }
            }
        }

        if (attributeName.isNotEmpty() && attributeValue.isNotEmpty()) {
            attributes[attributeName.toString()] = attributeValue.toString().toLong()
        }

        return attributes
    }

    private fun getCardName(): String =
        try {
            if (operation == "show" || lastCardName.isBlank()) {
                CharacterDao.getActiveCharacterSheetName(sender.id, context.getGroupIdOrZero())
            } else {
                lastCardName
            }
        } catch (_: Exception) {
            ""
        }

    private fun Map<String, Long>.formatAttributes(columnsPerLine: Int = 4): String {
        return this.entries
            .map { "${it.key} ${it.value}" }
            .chunked(columnsPerLine)
            .joinToString("\n") { it.joinToString(" ") }
    }


    override fun generateKeywordReplacements() = mapOf(
        "CharacterSheetName" to getCardName(),
        "AttributeMap" to attributeMap.formatAttributes(),
        "AttributeName" to lastCardName,
        "AttributeValue" to attributeValue,
    )

    override val helpDescription =
        VanillaStringContent.MODULE_SET_ATTRIBUTE_DESC
    override val helpContent =
        VanillaStringContent.MODULE_SET_ATTRIBUTE_CONTENT
    override val commandPrefix =
        "st"

    companion object {
        const val DEFAULT_CHARACTER_SHEET_NAME = "默认"
        const val DEFAULT_CHARACTER_SHEET_DESCRIPTION = "默认简介"
    }
}