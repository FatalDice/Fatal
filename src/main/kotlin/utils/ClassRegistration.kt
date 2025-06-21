package uk.akane.fatal.utils

import uk.akane.fatal.module.BotModule
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.module.HelpModule
import uk.akane.fatal.module.RollModule

object ClassRegistration {
    val commandModuleClasses: List<Class<out CommandModule>> = listOf(
        BotModule::class.java,
        HelpModule::class.java,
    )
}