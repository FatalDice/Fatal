package uk.akane.fatal.module.roll

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.data.VanillaStringContent.MODULE_ROLL_CONTENT
import uk.akane.fatal.data.VanillaStringContent.MODULE_ROLL_DESC
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.module.roll.Evaluator.RollException
import uk.akane.fatal.module.roll.Evaluator.RollNumberLessThanOneException
import uk.akane.fatal.module.roll.Evaluator.RollNumberNotIntegerException
import uk.akane.fatal.module.roll.Evaluator.RollNumberOutOfBoundsException
import uk.akane.fatal.utils.MessageUtils

class RollModule : CommandModule {

    private val evaluator = Evaluator()
    private var lastParameter = ""

    private lateinit var sender: Contact

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        lastParameter = parameter
        this.sender = sender

        try {
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.ROLL_RESULT_INFO,
                    this
                )
            )
        } catch (e: RollNumberNotIntegerException) {
            dispatcher.logger.info("Rolling integer exception", e)
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.ROLL_INTEGER_ERROR,
                    this,
                    false
                )
            )
        } catch (e: RollNumberLessThanOneException) {
            dispatcher.logger.info("Rolling less than one exception", e)
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.ROLL_NEGATIVE_ERROR,
                    this,
                    false
                )
            )
        } catch (e: RollNumberOutOfBoundsException) {
            dispatcher.logger.info("Rolling out of range", e)
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.ROLL_OUT_OF_BOUND_ERROR,
                    this,
                    false
                )
            )
        } catch (e: RollException) {
            dispatcher.logger.info("Rolling exception", e)
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.ROLL_ERROR,
                    this,
                    false
                )
            )
        } catch (e: Exception) {
            dispatcher.logger.info("Expression error", e)
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.ROLL_EXPRESSION_ERROR,
                    this,
                    false
                )
            )
        }
    }

    override val commandPrefix: String
        get() = "r"

    override fun generateKeywordReplacements() = mapOf(
        "SenderName" to MessageUtils.getSenderName(sender),
        "RollResult" to evaluator.evaluateFormatted(lastParameter)
    )

    override val helpDescription = MODULE_ROLL_DESC
    override val helpContent = MODULE_ROLL_CONTENT

}