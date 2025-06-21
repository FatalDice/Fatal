package uk.akane.fatal.module

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher

class RollModule : CommandModule {
    override suspend fun invoke(
        event: Event,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        // val expressionCore = Expressions()
        // val result = expressionCore.evalToString(preprocessDiceExpression(parameter))
        // contact.sendMessage(expressionCore.getCalculationSteps() + " = " + result)
    }

    fun preprocessDiceExpression(expression: String): String {
        return expression.replace(Regex("(?<=^|[^0-9])d([0-9]+)")) { matchResult ->
            "1d${matchResult.groupValues[1]}"
        }
    }

    override val commandPrefix: String
        get() = "r"
}