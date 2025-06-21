package uk.akane.fatal.module

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.utils.VersionUtils

class BotModule : CommandModule {
    override suspend fun invoke(
        event: Event,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        contact.sendMessage("${VersionUtils.getPluginVersionHeader()}\n输入 .help 查看帮助信息\n\n" +
            "${VersionUtils.getCompilationTime()}\n" +
            "Running with ${VersionUtils.getJdkVersion()} (${VersionUtils.getOSName()})\n" +
            "自豪地使用 GPL-3.0 协议开源:\n" +
            VersionUtils.getOpenSourceAddress()
        )
    }

    override val commandPrefix: String = "bot"
}