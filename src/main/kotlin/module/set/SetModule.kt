package uk.akane.fatal.module.set

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.DiceUtils
import uk.akane.fatal.utils.MessageUtils

open class SetModule : CommandModule {
    private var variableName = ""
    private var variableKey = ""
    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        variableName =
            if (isSetVariable(parameter) && getVariableName().isBlank())
                parameter.substringBefore(' ').trim().lowercase()
            else getVariableName().ifBlank { parameter.lowercase() }
        variableKey =
            if (isSetVariable(parameter) && getVariableName().isBlank())
                parameter.substringAfter(' ').trim()
            else if (getVariableName().isBlank())
                ""
            else
                parameter
        try {
            when (variableName) {
                "nickname" -> {
                    MessageUtils.setNickName(sender, contact, variableKey)
                }

                "defaultdice" -> {
                    DiceUtils.setDefaultDice(contact, variableKey.toLongOrNull())
                }

                else -> throw IllegalArgumentException("Unknown variable name")
            }
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    if (isSetVariable(parameter))
                        getSetSuccessfulString()
                    else
                        getUnsetSuccessfulString(),
                    this

                )
            )
        } catch (_: IllegalArgumentException) {
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_UNSUCCESSFUL_VARIABLE_NOT_FOUND,
                    this
                )
            )
        } catch (_: Exception) {
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    getSetIllegalKeyString(),
                    this
                )
            )
        }
    }

    open fun getSetSuccessfulString() =
        VanillaStringContent.StringTypes.SET_SET_SUCCESSFUL

    open fun getUnsetSuccessfulString() =
        VanillaStringContent.StringTypes.SET_UNSET_SUCCESSFUL

    open fun getSetIllegalKeyString() =
        VanillaStringContent.StringTypes.SET_UNSUCCESSFUL_ILLEGAL_KEY

    open fun getVariableName() =
        ""

    private fun isSetVariable(parameter: String) =
        if (getVariableName().isEmpty()) parameter.trim().contains(' ') else parameter.isNotBlank()

    override fun generateKeywordReplacements(): Map<String, String> = mapOf(
        "VariableName" to variableName,
        "VariableKey" to variableKey
    )

    override val helpDescription: String =
        VanillaStringContent.MODULE_SET_DESC
    override val helpContent =
        VanillaStringContent.MODULE_SET_CONTENT
    override val commandPrefix =
        "set"
}