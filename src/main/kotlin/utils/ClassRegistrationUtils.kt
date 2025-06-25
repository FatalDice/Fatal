package uk.akane.fatal.utils

import uk.akane.fatal.module.bot.BotModule
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.module.help.HelpModule
import uk.akane.fatal.module.nickname.NicknameModule
import uk.akane.fatal.module.roll.RollModule

object ClassRegistrationUtils {
    val commandModuleClasses: List<Class<out CommandModule>> = listOf(
        BotModule::class.java,
        HelpModule::class.java,
        RollModule::class.java,
        NicknameModule::class.java
    )
}