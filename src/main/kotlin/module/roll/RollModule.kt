package uk.akane.fatal.module.roll

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.module.CommandModule

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
        this.sender = contact
        contact.sendMessage(
            dispatcher.translator.getTranslation(
                VanillaStringContent.StringTypes.ROLL_RESULT_INFO,
                this
            )
        )
    }

    override val commandPrefix: String
        get() = "r"

    override fun generateKeywordReplacements() = mapOf(
        "SenderName" to if (sender is Friend) (sender as Friend).nick else if (sender is Member) (sender as Member).nameCard else "",
        "RollResult" to evaluator.evaluateFormatted(lastParameter)
    )
}