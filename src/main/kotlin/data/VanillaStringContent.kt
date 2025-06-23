package uk.akane.fatal.data

object VanillaStringContent {
    enum class StringTypes {
        HELP_WELCOME_BANNER,
        HELP_OPENSOURCE_INFORMATION,
        HELP_COMMAND_LIST,
        HELP_MAIN_PAGE,
        BOT_MESSAGE
    }
    const val HELP_WELCOME_BANNER = "基于 Kotlin 实现的高机能 TRPG 掷骰机器人"
    const val HELP_OPENSOURCE_INFORMATION = "Keelar/ExprK"
    const val HELP_COMMAND_LIST = "指令列表"
    const val HELP_MAIN_PAGE = "{PluginVersionHeader}\n\n{HelpWelcomeBanner}\n\n输入 .bot 查看版本信息"
    const val BOT_MESSAGE = "{PluginVersionHeader}\n输入 .help 查看帮助信息\n\n{CompilationTime}\nRunning with {JdkVersion} ({OSName})\n自豪地使用 AGPL-3.0 协议开源:\n{OpenSourceAddress}"
}