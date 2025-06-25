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

    private var times: Int = 0
    private var innerExpr: String = ""

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
            val countMatch = Regex("""^(\d+)#(.*)""").matchEntire(parameter.trim())
            times = countMatch?.groupValues[1]?.toInt() ?: 0
            innerExpr = countMatch?.groupValues[2] ?: ""

            if (times > EXECUTION_TIMES_MAX) {
                throw ArithmeticException("Execution times is more than $EXECUTION_TIMES_MAX")
            }

            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    if (countMatch == null)
                            VanillaStringContent.StringTypes.ROLL_RESULT_INFO_SINGLE
                    else
                        VanillaStringContent.StringTypes.ROLL_RESULT_INFO_MULTI,
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
        } catch (e: ArithmeticException) {
            dispatcher.logger.info("Arithmetic exception", e)
            contact.sendMessage(
                dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.ROLL_COUNT_OUT_OF_BOUND_ERROR,
                    this,
                    false
                )
            )
        } catch (e: UnsupportedOperationException) {
            // DEBUG
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
        "RollResult" to evaluator.evaluateFormatted(lastParameter),
        "DiceCount" to times.toString(),
        "MultiRollResult" to (1..times).joinToString("\n") { evaluator.evaluateFormatted(innerExpr) }
    )

    override val helpDescription = MODULE_ROLL_DESC
    override val helpContent = MODULE_ROLL_CONTENT

    companion object {
        const val EXECUTION_TIMES_MAX = 20
    }

}