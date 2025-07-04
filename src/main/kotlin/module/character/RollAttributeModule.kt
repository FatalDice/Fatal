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
import uk.akane.fatal.utils.DiceUtils
import uk.akane.fatal.utils.MessageUtils
import uk.akane.fatal.utils.getGroupIdOrZero
import uk.akane.fatal.utils.isNumeric
import uk.akane.fatal.utils.parseParameters

class RollAttributeModule : CommandModule {

    private lateinit var sender: Contact
    private lateinit var context: Contact
    private lateinit var dispatcher: Dispatcher
    private var successRate: Int = 0
    private var attributeName: String = ""
    private var reason: String? = ""
    private var rollResult: VanillaStringContent.StringTypes = VanillaStringContent.StringTypes.EMPTY
    private var evaluationResult = ""

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        this.sender = sender
        this.context = contact
        this.dispatcher = dispatcher
        val (param1, param2, param3) = parameter.parseParameters(3)

        try {
            when {
                param1.isNumeric() -> {
                    successRate = param1.toInt()
                    attributeName = ""
                    reason = param2.ifBlank { param3 }
                }

                param2.isNumeric() -> {
                    attributeName = param1
                    successRate = param2.toInt()
                    reason = param3
                }

                param2.isNotBlank() -> {
                    attributeName = param1
                    successRate = CharacterDao.getAttributeForCharacterSheet(
                        sender.id,
                        contact.getGroupIdOrZero(),
                        attributeName
                    ).toInt()
                    reason = param2
                }

                else -> {
                    attributeName = param1
                    successRate = CharacterDao.getAttributeForCharacterSheet(
                        sender.id,
                        contact.getGroupIdOrZero(),
                        attributeName
                    ).toInt()
                    reason = ""
                }
            }

            val expression = "1d100"
            val (result, _) = DiceUtils.evaluateExpressionRaw(expression, contact)
            val rollValue = result.first
            evaluationResult = "[$rollValue] " + (if (rollValue <= successRate) "≤" else ">") + " $successRate"

            val fumbleThreshold = if (successRate >= 50) 100 else 96

            rollResult = when {
                rollValue <= 1 -> VanillaStringContent.StringTypes.ROLL_RESULT_CRITICAL_SUCCESS
                rollValue <= successRate / 5 -> VanillaStringContent.StringTypes.ROLL_RESULT_EXTREME_SUCCESS
                rollValue <= successRate / 2 -> VanillaStringContent.StringTypes.ROLL_RESULT_HARD_SUCCESS
                rollValue <= successRate -> VanillaStringContent.StringTypes.ROLL_RESULT_REGULAR_SUCCESS
                rollValue < fumbleThreshold -> VanillaStringContent.StringTypes.ROLL_RESULT_FAILURE
                else -> VanillaStringContent.StringTypes.ROLL_RESULT_FAILURE
            }

            if (!reason.isNullOrBlank()) {
                contact.sendMessage(
                    dispatcher.translator.getTranslation(
                        VanillaStringContent.StringTypes.ROLL_ATTRIBUTE_REPLY_WITH_REASON,
                        this
                    )
                )
            } else {
                contact.sendMessage(
                    dispatcher.translator.getTranslation(
                        VanillaStringContent.StringTypes.ROLL_ATTRIBUTE_REPLY,
                        this
                    )
                )
            }
        } catch (_: CharacterSheetNoDefaultException) {
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.SET_ATTRIBUTE_ACTIVE_CHARACTER_SHEET_NOT_FOUND,
                    this,
                    false
                )
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
        } catch (e: Exception) {
            contact.sendMessage("发生了未知错误：${e.localizedMessage}")
        }
    }

    override fun generateKeywordReplacements() = mapOf(
        "SenderName" to MessageUtils.getSenderName(sender, context),
        "Evaluation" to evaluationResult,
        "Result" to dispatcher.translator.getTranslation(rollResult, this, false),
        "RollReason" to (reason ?: "")
    )

    override val helpDescription =
        VanillaStringContent.MODULE_ROLL_ATTRIBUTE_DESC
    override val helpContent =
        VanillaStringContent.MODULE_ROLL_ATTRIBUTE_CONTENT
    override val commandPrefix =
        "ra"
}