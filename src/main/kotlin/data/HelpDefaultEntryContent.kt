package uk.akane.fatal.data

import uk.akane.fatal.module.help.HelpEntry

object HelpDefaultEntryContent {
    val defaultEntryList: List<HelpEntry> = listOf(
        HelpEntry("开源", "开放源代码的相关描述", VanillaStringContent.HELP_OPENSOURCE_INFORMATION),
        HelpEntry("联系", "联系作者", VanillaStringContent.HELP_CONTACT),
    )
}