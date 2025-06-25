package uk.akane.fatal.module.help

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.utils.capitalize
import uk.akane.fatal.components.Dispatcher
import uk.akane.fatal.components.Trie
import uk.akane.fatal.data.HelpDefaultEntryContent
import uk.akane.fatal.data.VanillaStringContent
import uk.akane.fatal.data.VanillaStringContent.HELP_COMMAND_LIST
import uk.akane.fatal.data.VanillaStringContent.HELP_COMMAND_LIST_DESC
import uk.akane.fatal.data.VanillaStringContent.HELP_COMMAND_LIST_FOOTER
import uk.akane.fatal.data.VanillaStringContent.HELP_COMMAND_LIST_TITLE
import uk.akane.fatal.data.VanillaStringContent.MODULE_HELP_CONTENT
import uk.akane.fatal.data.VanillaStringContent.MODULE_HELP_DESC
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.VersionUtils
import kotlin.math.max

class HelpModule: CommandModule {

    private var dispatcher: Dispatcher? = null

    private val databaseTrie: Trie<HelpEntry> = Trie()
    private val primaryTrie: Trie<HelpEntry> = Trie()
    private val moduleTrie: Trie<HelpEntry> = Trie()

    enum class HelpTrieTypes {
        DATABASE, PRIMARY, MODULE
    }

    private val searchOrder = listOf(
        HelpTrieTypes.DATABASE,
        HelpTrieTypes.PRIMARY,
        HelpTrieTypes.MODULE
    )

    override suspend fun invoke(
        event: Event,
        sender: Contact,
        contact: Contact,
        parameter: String,
        dispatcher: Dispatcher
    ) {
        this.dispatcher = dispatcher

        val entry = searchOrder.firstNotNullOfOrNull { order ->
            dispatcher.logger.debug("Start searching for $parameter in $order")
            val actualTrie = getHelpTrieTypeMap()[order]
                ?: throw NoSuchElementException("Requested trie not found!")
            actualTrie.find(parameter)?.also {
                dispatcher.logger.debug("Found entry:\n$it")
            }
        }

        dispatcher.logger.debug("Entry: ${entry}")

        contact.sendMessage(
            entry?.content
                ?: dispatcher.translator.getTranslation(
                    VanillaStringContent.StringTypes.HELP_MAIN_PAGE,
                    this
                )
        )
    }

    fun initializeHelpEntry(dispatcher: Dispatcher) {
        // Load from default entry
        HelpDefaultEntryContent.defaultEntryList.forEach { entry ->
            primaryTrie.insert(entry.entry, entry)
        }
        // Load from modules
        dispatcher.getCommandReferenceList().forEach { commandModule ->
            moduleTrie.insert(
                commandModule.commandPrefix,
                HelpEntry(
                    commandModule.commandPrefix,
                    commandModule.helpDescription,
                    String.format(
                        VanillaStringContent.HELP_MODULE_INDICATOR,
                        commandModule.commandPrefix.capitalize()
                    ) + "\n\n" +
                        commandModule.helpContent
                )
            )
        }
        // Compile command list
        val commandListBuilder = StringBuilder()
        commandListBuilder.append(HELP_COMMAND_LIST_TITLE)
            .append("\n\n")

        var longestCommandLength = 0
        dispatcher.getCommandReferenceList().forEach { commandModule ->
            longestCommandLength = max(longestCommandLength, commandModule.commandPrefix.length)
        }

        // Format each entry
        dispatcher.getCommandReferenceList().forEach { commandModule ->
            commandListBuilder.append("/${commandModule.commandPrefix} ")
                .append(" ".repeat(longestCommandLength + 1 - commandModule.commandPrefix.length))
                .append(" - ${commandModule.helpDescription}\n")
        }
        commandListBuilder.append('\n')
            .append(String.format(
                VanillaStringContent.HELP_COMMAND_LIST_COUNT_FOOTER,
                dispatcher.getCommandReferenceList().size)
            )
            .append('\n')
            .append(HELP_COMMAND_LIST_FOOTER)

        // Finally insert into database
        databaseTrie.insert(
            HELP_COMMAND_LIST,
            HelpEntry(
                HELP_COMMAND_LIST,
                HELP_COMMAND_LIST_DESC,
                commandListBuilder.toString()
            )
        )
    }

    private fun getHelpTrieTypeMap() = mapOf(
        HelpTrieTypes.DATABASE to databaseTrie,
        HelpTrieTypes.PRIMARY to primaryTrie,
        HelpTrieTypes.MODULE to moduleTrie,
    )

    override val commandPrefix: String
        get() = "help"

    override fun generateKeywordReplacements() = mapOf(
            "PluginVersionHeader" to VersionUtils.getPluginVersionHeader(),
            "HelpWelcomeBanner" to (dispatcher?.translator?.getTranslation(VanillaStringContent.StringTypes.HELP_WELCOME_BANNER) ?: ""),
        )

    override val helpDescription = MODULE_HELP_DESC
    override val helpContent = MODULE_HELP_CONTENT

}