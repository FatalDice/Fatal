package uk.akane.fatal.data

object VanillaStringContent {

    // Translatable string types
    enum class StringTypes {
        HELP_WELCOME_BANNER,
        HELP_OPENSOURCE_INFORMATION,
        HELP_CONTACT,
        HELP_COMMAND_LIST,
        HELP_COMMAND_LIST_FOOTER,
        HELP_MAIN_PAGE,
        BOT_MESSAGE,
        ROLL_RESULT_INFO_SINGLE,
        ROLL_RESULT_INFO_MULTI,
        ROLL_ERROR,
        ROLL_EXPRESSION_ERROR,
        ROLL_INTEGER_ERROR,
        ROLL_NEGATIVE_ERROR,
        ROLL_OUT_OF_BOUND_ERROR,
        ROLL_COUNT_OUT_OF_BOUND_ERROR,
    }

    // Module translatable strings

    // Help
    const val HELP_WELCOME_BANNER = "基于 Kotlin 实现的高机能 TRPG 掷骰机器人"
    const val HELP_OPENSOURCE_INFORMATION = "Fatal! Dice 基于这些项目编写而成:\n\nmamoe/Mirai\nMrXiaoM/overflow\nJetBrains/Kotlin\n\n本项目自豪地使用 AGPL-3.0 协议开源，有关 AGPL-3.0 协议的详细信息，请翻阅: https://www.gnu.org/licenses/agpl-3.0.en.html\n如有鸣谢缺失，请联系开发者。"
    const val HELP_CONTACT = "Fatal! Dice 用户交流群: 709707258\n\n有 BUG 或者建议想要反馈，也可以到官方 Github 上提交 Issue。"
    const val HELP_COMMAND_LIST = "指令"
    const val HELP_COMMAND_LIST_DESC = "查看指令列表"
    const val HELP_COMMAND_LIST_TITLE = "Fatal! Dice 指令列表:"
    const val HELP_COMMAND_LIST_COUNT_FOOTER = "共 %s 项条目。"
    const val HELP_COMMAND_LIST_FOOTER = "本帮助页面为自动生成。"
    const val HELP_MODULE_INDICATOR = "正在显示 %s 的帮助条目:"
    const val HELP_MAIN_PAGE = "{PluginVersionHeader}\n\n{HelpWelcomeBanner}\n\n/help 指令  获取指令列表\n/help 开源  查看开源信息\n/help 联系  作者联系方式\n\n输入 /bot 查看版本信息"

    // Bot
    const val BOT_MESSAGE = "{PluginVersionHeader}\n输入 /help 查看帮助信息\n\n{CompilationTime}\nRunning with {JdkVersion} ({OSName})\n自豪地使用 AGPL-3.0 协议开源:\n{OpenSourceAddress}"

    // Roll
    const val ROLL_RESULT_INFO_SINGLE = "{SenderName}投掷出了一枚骰子:\n{RollResult}"
    const val ROLL_RESULT_INFO_MULTI = "{SenderName}投掷出了 {DiceCount} 枚骰子:\n{MultiRollResult}"
    const val ROLL_ERROR = "掷骰错误!"
    const val ROLL_EXPRESSION_ERROR = "表达式错误!"
    const val ROLL_INTEGER_ERROR = "表达式不是整型!"
    const val ROLL_NEGATIVE_ERROR = "掷骰参数小于1!"
    const val ROLL_OUT_OF_BOUND_ERROR = "掷骰参数超出最大值!"
    const val ROLL_COUNT_OUT_OF_BOUND_ERROR = "计算次数超过限定值!"


    // Module desc.
    const val MODULE_HELP_DESC = "显示帮助命令"
    const val MODULE_HELP_CONTENT =
        "· 显示主要帮助命令\n" +
        " - [/help] 显示主要帮助命令\n\n" +
        "· 显示帮助词条\n" +
        " - [/help <词条名称>] 显示对应帮助词条\n\n" +
        "* 注: 在输入参数时请不要包括范例中的 <> ，此符号仅作区分用。"

    const val MODULE_BOT_DESC = "显示机器人信息"
    const val MODULE_BOT_CONTENT =
        "· 显示机器人信息\n" +
        " - [/bot] 打印所有信息\n\n" +
        "· 开启/关闭 机器人\n" +
        " - [/bot on] 开启机器人\n" +
        " - [/bot off] 关闭机器人"

    const val MODULE_ROLL_DESC = "掷出骰子"
    const val MODULE_ROLL_CONTENT =
        "· 掷出骰子\n" +
        " - [/r 3d6] 掷出 3 枚 6 面的骰子\n\n" +
        "· 数学计算\n" +
        " - [/r 3 ^ 3] 计算 3 的 3 次方\n" +
        " - [/r 3 d ((3 + 6) * 5)] 投掷 3 枚面数为 ((3 + 6) * 5) 的骰子\n\n" +
        "* 数学计算支持 +, -, *, /, %, ^, √ 以及括号代表的优先级运算\n" +
        "* 最大掷骰面数和次数为 1,000,000\n" +
        "* 掷骰次数超过 50，掷骰细节将被隐藏"
}