package uk.akane.fatal

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.info
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.data.database.DatabaseFactory

object FatalPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "uk.akane.fatal",
        name = "宿命 Dice",
        version = BuildConstants.MAJOR_VERSION
    ) {
        author(BuildConstants.AUTHOR)
        info(
            """
            强大的骰子机器人
        """.trimIndent()
        )
    }
) {

    private val dispatcher = Dispatcher(logger)

    override fun onEnable() {
        // Initialize
        dispatcher.initialize()
        DatabaseFactory.init()

        logger.info { "Plugin loaded" }

        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.subscribeAlways<GroupMessageEvent> {
            dispatcher.dispatch(it, sender, group, message)
        }
        eventChannel.subscribeAlways<FriendMessageEvent> {
            dispatcher.dispatch(it, sender, sender, message)
        }
        eventChannel.subscribeAlways<NewFriendRequestEvent> {
            accept()
        }
        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            accept()
        }
        eventChannel.subscribeAlways<OtherClientMessageEvent> {
            dispatcher.dispatch(it, sender, sender, message)
        }
    }

    override fun onDisable() {
        super.onDisable()
        dispatcher.cleanup()
    }
}
