package uk.akane.fatal.data

@Suppress("Unused")
object VanillaStringContent {

    // Translatable string types
    enum class StringTypes {
        EMPTY,
        HELP_WELCOME_BANNER,
        HELP_OPENSOURCE_INFORMATION,
        HELP_CONTACT,
        HELP_COMMAND_LIST,
        HELP_COMMAND_LIST_FOOTER,
        HELP_MAIN_PAGE,
        HELP_NOT_FOUND,
        BOT_MESSAGE,
        ROLL_RESULT_INFO_SINGLE,
        ROLL_RESULT_INFO_MULTI,
        ROLL_ERROR,
        ROLL_EXPRESSION_ERROR,
        ROLL_INTEGER_ERROR,
        ROLL_NEGATIVE_ERROR,
        ROLL_OUT_OF_BOUND_ERROR,
        ROLL_COUNT_OUT_OF_BOUND_ERROR,
        NICKNAME_SET,
        NICKNAME_UNSET,
        SET_SET_SUCCESSFUL,
        SET_UNSET_SUCCESSFUL,
        SET_UNSUCCESSFUL_VARIABLE_NOT_FOUND,
        SET_UNSUCCESSFUL_ILLEGAL_KEY,
        DEFAULT_DICE_SET,
        DEFAULT_DICE_UNSET,
        DEFAULT_DICE_ILLEGAL,
        RULESET_ILLEGAL_OPERATION,
        RULESET_ILLEGAL_RULESET_VALUE,
        RULESET_INSERT_COMPLETE,
        RULESET_DELETE_COMPLETE,
        RULESET_LIST,
        RULESET_GENERATION,
        CHARACTER_SHEET_NOT_FOUND,
        CHARACTER_SHEET_SET_DEFAULT,
        CHARACTER_SHEET_SET_SWITCH,
        CHARACTER_SHEET_LIST_GENERAL,
        CHARACTER_SHEET_CREATE_SUCCESSFUL,
        CHARACTER_SHEET_DELETE_SUCCESSFUL,
        CHARACTER_SHEET_RENAME_SUCCESSFUL,
        SET_ATTRIBUTE_CLEAR_CHARACTER_SHEET_NOT_FOUND,
        SET_ATTRIBUTE_ACTIVE_CHARACTER_SHEET_NOT_FOUND,
        SET_ATTRIBUTE_INSERT_SUCCESSFUL,
        SET_ATTRIBUTE_DELETE_SUCCESSFUL,
        SET_ATTRIBUTE_ATTRIBUTE_NOT_FOUND,
        SET_ATTRIBUTE_ATTRIBUTE_DISPLAY,
        SET_ATTRIBUTE_ATTRIBUTE_MAP_DISPLAY,
        ROLL_ATTRIBUTE_REPLY,
        ROLL_ATTRIBUTE_REPLY_WITH_REASON,
        ROLL_RESULT_REGULAR_SUCCESS,
        ROLL_RESULT_HARD_SUCCESS,
        ROLL_RESULT_EXTREME_SUCCESS,
        ROLL_RESULT_CRITICAL_SUCCESS,
        ROLL_RESULT_FAILURE,
        ROLL_RESULT_FUMBLE,
    }

    // Module translatable strings
    const val EMPTY = ""

    // Help
    const val HELP_WELCOME_BANNER = "基于 Kotlin 实现的高机能 TRPG 掷骰机器人"
    const val HELP_OPENSOURCE_INFORMATION =
        "宿命 Dice 基于这些项目编写而成:\n\nmamoe/Mirai\nMrXiaoM/overflow\nJetBrains/Kotlin\n\n本项目自豪地使用 AGPL-3.0 协议开源，有关 AGPL-3.0 协议的详细信息，请翻阅: https://www.gnu.org/licenses/agpl-3.0.en.html\n如有鸣谢缺失，请联系开发者。"
    const val HELP_CONTACT =
        "宿命 Dice 用户交流群: 709707258\n\n有 BUG 或者建议想要反馈，也可以到官方 Github 上提交 Issue。"
    const val HELP_SERVICE_AGREEMENT =
        "· 协议适用\n - 阅读本协议、邀请骰娘、或使用任意服务即视为同意本协议内容\n - 若不同意，请使用 .dismiss 命令将骰娘移出群聊\n\n· 用户行为规范\n - 禁止禁言、踢出骰娘或刷屏式掷骰行为\n - 滥用行为将记录并可能导致服务中断\n - 使用 .bot on/off 控制骰娘启用状态\n\n· 群邀请责任\n - 骰娘默认自动接受群邀请\n - 若被邀请至不友善环境，邀请者承担连带责任\n\n· 使用限制\n - 严禁用于赌博、诈骗等违法用途\n - 对敏感内容（如昵称）服务可能会自动拒绝\n\n· 服务可用性说明\n - 由于服务器和资金限制，服务不保证100%可用\n - 停机期间无响应，但不友善行为依旧视为违约\n\n· 协议违约处理\n - 严重违规行为将导致服务终止与拉黑\n - 黑名单可协商，但骰主拥有最终裁定权\n\n· 协议变更与通知\n - 协议条款可能随时变动，请关注 .help、签名、空间或官方群通知\n\n· 服务费用\n - 掷骰服务完全免费，欢迎自愿投食支持\n\n· 开源与授权协议\n - 若使用本服务的源代码或部署服务，必须遵守 AGPL-3.0 协议\n - 包括开源衍生项目、部署时的源码公开与原始署名保留\n - 违规使用将视为严重违约，骰主保留维权权利\n\n· 最终解释权\n - 本服务最终解释权归骰主（服务提供方）所有\n\n* 注: 在阅读本协议时请注意行为后果，使用即代表已完全接受本协议。"
    const val HELP_COMMAND_LIST = "指令"
    const val HELP_COMMAND_LIST_DESC = "查看指令列表"
    const val HELP_COMMAND_LIST_TITLE = "宿命 Dice 指令列表:"
    const val HELP_COMMAND_LIST_COUNT_FOOTER = "共 %s 项条目。"
    const val HELP_COMMAND_LIST_FOOTER = "本帮助页面为自动生成。"
    const val HELP_MODULE_INDICATOR = "正在显示 %s 的帮助条目:"
    const val HELP_MAIN_PAGE =
        "{PluginVersionHeader}\n\n{HelpWelcomeBanner}\n\n/help 指令  获取指令列表\n/help 协议  查看使用协议\n/help 开源  查看开源信息\n/help 联系  作者联系方式\n\n输入 /bot 查看版本信息"
    const val HELP_NOT_FOUND = "帮助条目{HelpEntry}未找到。"

    // Bot
    const val BOT_MESSAGE =
        "{PluginVersionHeader}\n输入 /help 查看帮助信息\n\n{CompilationTime}\nRunning with {JdkVersion} ({OSName})\n自豪地使用 AGPL-3.0 协议开源:\n{OpenSourceAddress}"

    // Roll
    const val ROLL_RESULT_INFO_SINGLE = "{SenderName}投掷出了一枚骰子:\n{RollResult}"
    const val ROLL_RESULT_INFO_MULTI = "{SenderName}投掷出了 {DiceCount} 枚骰子:\n{MultiRollResult}"
    const val ROLL_ERROR = "掷骰错误!"
    const val ROLL_EXPRESSION_ERROR = "表达式错误!"
    const val ROLL_INTEGER_ERROR = "表达式不是整型!"
    const val ROLL_NEGATIVE_ERROR = "掷骰参数小于1!"
    const val ROLL_OUT_OF_BOUND_ERROR = "掷骰参数超出最大值!"
    const val ROLL_COUNT_OUT_OF_BOUND_ERROR = "计算次数超过限定值!"

    // Set
    const val SET_SET_SUCCESSFUL = "已将{VariableName}设置为{VariableKey}。"
    const val SET_UNSET_SUCCESSFUL = "已将{VariableName}的值清除。"
    const val SET_UNSUCCESSFUL_VARIABLE_NOT_FOUND = "目标变量{VariableName}未找到!"
    const val SET_UNSUCCESSFUL_ILLEGAL_KEY = "键值{VariableKey}非法!"

    // Nickname
    const val NICKNAME_SET = "已为{SenderName}设置昵称{NickName}。"
    const val NICKNAME_UNSET = "已为{SenderName}清除昵称。"

    // Default Dice
    const val DEFAULT_DICE_SET = "已将默认骰面设置为{DefaultDice}!"
    const val DEFAULT_DICE_UNSET = "已将默认骰面清楚!"
    const val DEFAULT_DICE_ILLEGAL = "骰面键值{DefaultDice}非法!"

    // Ruleset
    const val RULESET_ILLEGAL_OPERATION = "规则组操作非法!"
    const val RULESET_ILLEGAL_RULESET_VALUE = "规则组参数非法!"
    const val RULESET_INSERT_COMPLETE = "已插入规则组{RulesetName}。"
    const val RULESET_DELETE_COMPLETE = "已删除规则组{RulesetName}。"
    const val RULESET_LIST = "以下是记录的规则组列表:\n{RulesetList}"
    const val RULESET_GENERATION = "生成了名为{RulesetName}的规则组:\n{RulesetGeneration}"

    // Character card
    const val CHARACTER_SHEET_NOT_FOUND = "名称为{CharacterSheetName}的角色卡未找到!"
    const val CHARACTER_SHEET_SET_DEFAULT = "已将{CharacterSheetName}设置为默认角色卡。"
    const val CHARACTER_SHEET_SET_SWITCH = "已将当前对话的角色卡设置为{CharacterSheetName}。"
    const val CHARACTER_SHEET_LIST_GENERAL = "{SenderName}的角色卡列表:\n\n{CharacterSheetList}\n\n* 注: 标有 * 的项目为已选中角色卡，标有 # 的项目为默认角色卡。"
    const val CHARACTER_SHEET_CREATE_SUCCESSFUL = "已创建名为{CharacterSheetName}的角色卡。"
    const val CHARACTER_SHEET_DELETE_SUCCESSFUL = "已删除名为{CharacterSheetName}的角色卡。"
    const val CHARACTER_SHEET_RENAME_SUCCESSFUL = "已将{CharacterSheetName}重命名为{CharacterSheetNewName}。"

    // Set attribute
    const val SET_ATTRIBUTE_CLEAR_CHARACTER_SHEET_NOT_FOUND = "未找到指定的角色卡{CharacterSheetName}。"
    const val SET_ATTRIBUTE_ACTIVE_CHARACTER_SHEET_NOT_FOUND = "未找到可用的角色卡。\n已创建默认角色卡。"
    const val SET_ATTRIBUTE_INSERT_SUCCESSFUL = "成功地向角色卡{CharacterSheetName}插入了属性:\n{AttributeMap}"
    const val SET_ATTRIBUTE_DELETE_SUCCESSFUL = "成功地清空了角色卡{CharacterSheetName}。"
    const val SET_ATTRIBUTE_ATTRIBUTE_NOT_FOUND = "未找到属性{AttributeName}!"
    const val SET_ATTRIBUTE_ATTRIBUTE_DISPLAY = "{CharacterSheetName}的属性{AttributeName}为{AttributeValue}"
    const val SET_ATTRIBUTE_ATTRIBUTE_MAP_DISPLAY = "{CharacterSheetName}的属性列表:\n{AttributeMap}"

    // Roll attribute
    const val ROLL_ATTRIBUTE_REPLY = "{SenderName}投掷了一枚骰子:\n{Evaluation} → {Result}"
    const val ROLL_ATTRIBUTE_REPLY_WITH_REASON = "{SenderName}因为{RollReason}投掷了一枚骰子:\n{Evaluation} → {Result}"
    const val ROLL_RESULT_REGULAR_SUCCESS = "成功"
    const val ROLL_RESULT_HARD_SUCCESS = "困难成功"
    const val ROLL_RESULT_EXTREME_SUCCESS = "极难成功"
    const val ROLL_RESULT_CRITICAL_SUCCESS = "大成功"
    const val ROLL_RESULT_FAILURE = "失败"
    const val ROLL_RESULT_FUMBLE = "大失败"

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
        "· 掷骰表达式\n" +
        " - [/r 3d6] 掷出 3 枚 6 面骰子\n" +
        " - [/r 4d8kh2] 掷出 4 枚 8 面骰子，保留其中最高的 2 个\n" +
        " - [/r 6d10kl3] 掷出 6 枚 10 面骰子，保留其中最低的 3 个\n" +
        " - [/r 2d20m15] 掷出 2 枚 20 面骰子，每个结果若低于 15 则提升至 15\n" +
        " - [/r 4d6<3] 掷出 4 枚 6 面骰子，小于 3 的将重新投掷一次\n" +
        " - [/r 4d6>5] 掷出 4 枚 6 面骰子，大于 5 的将重新投掷一次\n\n" +

        "· 数学表达式\n" +
        " - [/r (3 + 5) * 2] 支持基础四则运算、括号优先级\n" +
        " - [/r 2^3] 表示 2 的 3 次方\n" +
        " - 可使用运算符: +, -, *, /, %, ^，并支持括号嵌套\n\n" +

        "* 注: 可组合表达式，例如：[/r (4d6kh3 + 3) * 2]\n" +
        "* 注: 最大掷骰次数和面数：1,000,000\n" +
        "* 注: 掷骰次数超过 8 次，将省略单个掷骰结果\n"

    const val MODULE_SET_DESC = "设置变量"
    const val MODULE_SET_CONTENT =
        "· 设置变量\n" +
        " - [/set <变量名> <变量键值>] 设置变量\n" +
        " - [/set <变量名>] 清除变量\n\n" +
        "· 变量对照表\n" +
        " - [NickName] - 昵称\n" +
        " - [DefaultDice] - 默认骰面\n\n" +
        "* 注: 在输入参数时请不要包括范例中的 <> ，此符号仅作区分用。\n" +
        "* 注: 变量不分大小写。"

    const val MODULE_NICKNAME_DESC = "设置用户昵称"
    const val MODULE_NICKNAME_CONTENT =
        "· 设置用户昵称\n" +
        " - [/nn <用户昵称>] 设置用户昵称\n" +
        " - [/nn] 删除用户昵称\n\n" +
        "* 注: 在输入参数时请不要包括范例中的 <> ，此符号仅作区分用。"

    const val MODULE_DEFAULT_DICE_DESC = "设置默认骰子面数"
    const val MODULE_DEFAULT_DICE_CONTENT =
        "· 设置默认骰子面数\n" +
        " - [/dd <骰子面数>] 设置默认面数，如 20 表示默认使用 d20\n" +
        " - [/dd] 清除默认面数设置，恢复为系统默认值（d20）\n\n" +
        "* 注: 在输入参数时请不要包括范例中的 <> ，此符号仅作区分用。"

    const val MODULE_RULESET_DESC = "管理生成规则集"
    const val MODULE_RULESET_CONTENT =
        "· 管理生成规则集\n" +
        " - [/ruleset add <名字> <属性列表>]\n" +
        "   添加一个生成规则集\n" +
        " - [/ruleset remove <名字>]\n" +
        "   移除一个生成规则集\n" +
        " - [/ruleset list]\n" +
        "   列出所有生成规则集\n" +
        " - [/ruleset generate <名字>]\n" +
        "   生成一个生成规则集\n\n" +
        "· 属性列表格式\n" +
        " - [<变量名>=掷骰表达式]\n" +
        "   每个不同的生成规则之间用英文 \",\" 隔开\n\n" +
        "· 指令范例\n" +
        " - [/ruleset add coc INT=2d6+6, EDU=2d6+6]" +
        "   添加一个名称为 COC 的生成规则集，生成两个名为 INT 与 EDU 的元素\n\n" +
        "* 注: 在输入参数时请不要包括范例中的 <> ，此符号仅作区分用。"

    const val MODULE_COC_DESC = "快速生成 COC 作成"
    const val MODULE_COC_CONTENT =
        "· 生成 COC 作成\n" +
        " - [/coc <生成数量>] 生成一些 COC 的人物卡作成\n\n" +
        "* 注: 生成参数可忽略，这样只会生成 1 次人物卡作成。\n" +
        "* 注: 在输入参数时请不要包括范例中的 <> ，此符号仅作区分用。"

    const val MODULE_DND_DESC = "快速生成 DND 作成"
    const val MODULE_DND_CONTENT =
        "· 生成 DND 作成\n" +
        " - [/dnd <生成数量>] 生成一些 DND 的人物卡作成\n\n" +
        "* 注: 生成参数可忽略，这样只会生成 1 次人物卡作成。\n" +
        "* 注: 在输入参数时请不要包括范例中的 <> ，此符号仅作区分用。"

    const val MODULE_CHARACTER_SHEET_DESC = "切换和创建角色卡"
    const val MODULE_CHARACTER_SHEET_CONTENT =
        "· 切换使用角色卡\n" +
        " - [/cs default <角色卡名称>] 设置未设置群聊中的默认角色卡\n" +
        " - [/cs switch <角色卡名称>] 切换当前群聊/对话中使用的角色卡\n\n" +
        "· 编辑角色卡\n" +
        " - [/cs create <角色卡名称> <角色描述>] 创建角色卡\n" +
        " - [/cs delete <角色卡名称>] 永久删除角色卡\n" +
        " - [/cs rename <原角色卡名称> <现角色卡名称> <现角色卡描述 (可选)>] 重命名角色卡\n\n" +
        "· 列出角色卡\n" +
        " - [/cs list] 列出所有角色卡\n" +
        " - [/cs list <角色卡名称>] 列出选中角色卡的所有信息\n\n" +
        "* 注: 在输入参数时请不要包括范例中的 <> ，此符号仅作区分用。"

    const val MODULE_SET_ATTRIBUTE_DESC = "设置角色卡属性"
    const val MODULE_SET_ATTRIBUTE_CONTENT =
        "· 设置属性\n" +
        " - [/st 力量50外貌80] 将活跃角色卡的力量与外貌属性分别设置为 50 和 80\n" +
        " - [/st 力量50 <角色卡名称>] 将目标角色卡的力量属性设置为 50\n" +
        " - [/st clear] 删除活跃角色卡的所有内容\n" +
        " - [/st clear <角色卡名称>] 删除目标角色卡的所有内容\n\n" +
        "· 展示属性\n" +
        " - [/st show] 展示活跃角色卡的所有属性\n" +
        " - [/st show <属性名称>] 展示活跃角色卡的某一属性\n\n" +
        "* 注: 在输入参数时请不要包括范例中的 <> ，此符号仅作区分用。"

    const val MODULE_ROLL_ATTRIBUTE_DESC = "检定角色卡属性"
    const val MODULE_ROLL_ATTRIBUTE_CONTENT =
        "· 检定属性\n" +
        " - [/ra <成功率>] 检定无名称的属性\n" +
        " - [/ra <属性名称>] 检定当前角色卡中的目标属性\n" +
        " - [/ra <成功率> <检定原因>]" +
        " - [/ra <属性名称> <成功率> <检定原因>] 检定目标属性的同时增加原因\n\n" +
        "* 注: 在输入参数时请不要包括范例中的 <> ，此符号仅作区分用。"
}