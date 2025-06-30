package uk.akane.fatal.utils

import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.module.bot.BotModule
import uk.akane.fatal.module.help.HelpModule
import uk.akane.fatal.module.roll.RollModule
import uk.akane.fatal.module.ruleset.RulesetModule
import uk.akane.fatal.module.ruleset.alias.COCModule
import uk.akane.fatal.module.ruleset.alias.DNDModule
import uk.akane.fatal.module.set.SetModule
import uk.akane.fatal.module.set.alias.DefaultDiceModule
import uk.akane.fatal.module.set.alias.NicknameModule

object ClassRegistrationUtils {
    val commandModuleClasses: List<Class<out CommandModule>> = listOf(
        BotModule::class.java,
        HelpModule::class.java,
        RollModule::class.java,
        NicknameModule::class.java,
        SetModule::class.java,
        DefaultDiceModule::class.java,
        RulesetModule::class.java,
        COCModule::class.java,
        DNDModule::class.java,
    )
}