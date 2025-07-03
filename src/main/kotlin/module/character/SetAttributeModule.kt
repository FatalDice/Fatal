package uk.akane.fatal.module.character

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.data.database.profile.character.CharacterDao
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.CharacterSheetNoDefaultException
import uk.akane.fatal.utils.CharacterSheetNotFoundException
import uk.akane.fatal.utils.getGroupIdOrZero
import uk.akane.fatal.utils.parseParameters

class SetAttributeModule : CommandModule {

    private var lastCardName = ""
    private var attributeMap = mapOf<String, Long>()

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
        } catch (e: CharacterSheetNoDefaultException) {
            e.printStackTrace()
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_ATTRIBUTE_ACTIVE_CHARACTER_SHEET_NOT_FOUND,
                    this,
                    false
                )
            )
            return
        } catch (e: CharacterSheetNotFoundException) {
            e.printStackTrace()
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_ATTRIBUTE_CLEAR_CHARACTER_SHEET_NOT_FOUND,
                    this
                )
            )
            return
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

    private fun getCardName() =
        try {
            lastCardName.ifBlank {
                CharacterDao.getActiveCharacterSheetName(sender.id, context.getGroupIdOrZero())
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
        "AttributeMap" to attributeMap.formatAttributes()
    )

    override val helpDescription =
        VanillaStringContent.MODULE_SET_ATTRIBUTE_DESC
    override val helpContent =
        VanillaStringContent.MODULE_SET_ATTRIBUTE_CONTENT
    override val commandPrefix =
        "st"
}