package uk.akane.fatal.components

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.MiraiLogger
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.module.help.HelpModule
import uk.akane.fatal.utils.ClassRegistrationUtils
import kotlin.reflect.full.createInstance

class Dispatcher(val logger: MiraiLogger) {

    private val commandTrie = Trie<CommandModule>()
    private val commandReferenceList: MutableList<CommandModule> = mutableListOf()
    private val commandRegex = Regex("^[./,;\"'*()&^%$#@!。]")

    private val contextCache: MutableMap<String, Any> = mutableMapOf()
    val translator = Translator(this)

    fun initialize() {
        logger.verbose("Initializing module dispatcher")
        loadCommandModules()
        loadHelpDocuments()
    }

    fun cleanup() {
        logger.verbose("Cleaning module dispatcher")
        contextCache.clear()
    }

    fun getCommandReferenceList(): List<CommandModule> = commandReferenceList

    private fun loadCommandModules() {
        var trieCount = 0
        ClassRegistrationUtils.commandModuleClasses.forEach { clazz ->
            try {
                val module = clazz.kotlin.createInstance()
                commandTrie.insert(module.commandPrefix, module)
                commandReferenceList.add(module)
                logger.verbose("Loaded command module ${module.commandPrefix}")
                trieCount++
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        logger.info("Successfully loaded $trieCount command modules")
    }

    private fun loadHelpDocuments() {
        (commandTrie.find("help") as HelpModule).initializeHelpEntry(this)
    }

    suspend fun dispatch(event: Event, sender: Contact, contact: Contact, message: Message) {
        val userInput = message.contentToString().trim()
        if (userInput.isEmpty()) return

        val firstChar = userInput.firstOrNull()
        if (firstChar != null && commandRegex.matches(firstChar.toString())) {
            val commandContent = userInput.substring(1).trim()
            val match = commandTrie.findLongestPrefixMatch(commandContent)
            if (match != null) {
                val (commandModule, consumedLength) = match
                val restOfInput = commandContent.substring(consumedLength).trim()
                commandModule.invoke(event, sender, contact, restOfInput, this)
            } else {
                logger.verbose("No matching command found in Trie.")
            }
        } else {
            logger.verbose("Input does not start with a recognized command prefix.")
        }
    }

}
