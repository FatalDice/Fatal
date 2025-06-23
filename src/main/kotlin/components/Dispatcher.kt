package uk.akane.fatal.components

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.MiraiLogger
import uk.akane.fatal.module.CommandModule
import uk.akane.fatal.utils.ClassRegistration
import kotlin.reflect.full.createInstance

class Dispatcher(private val logger: MiraiLogger) {

    private val commandTrie = Trie()
    private val commandRegex = Regex("^[./,;\"'*()&^%$#@!。]")

    private val contextCache: MutableMap<String, Any> = mutableMapOf()
    private val translator = Translator(logger)

    fun initialize() {
        logger.warning("Initializing...")
        loadCommandModules()
    }

    fun cleanup() {
        logger.warning("Cleaning...")
        contextCache.clear()
    }

    private fun loadCommandModules() {
        ClassRegistration.commandModuleClasses.forEach { clazz ->
            try {
                val module = clazz.kotlin.createInstance() as CommandModule
                commandTrie.insert(module.commandPrefix, module)
                logger.info("Loaded command module ${module.commandPrefix}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        logger.info("Successfully loaded command modules")
    }

    suspend fun dispatch(event: Event, contact: Contact, message: Message) {
        val userInput = message.contentToString().trim()

        if (userInput.isEmpty()) return

        logger.info("User input: $userInput")

        if (commandRegex.containsMatchIn(userInput.substring(0, 1))) {
            val commandContent = userInput.substring(1).trim()

            logger.info("Checking for command $commandContent")

            var commandPrefix = ""

            for (i in 1..commandContent.length) {
                commandPrefix = commandContent.substring(0, i)

                val commandModule = commandTrie.find(commandPrefix)

                if (commandModule != null) {
                    logger.info("Found command module: ${commandModule.commandPrefix}")

                    val restOfInput = commandContent.substring(i).trim()
                    commandModule.invoke(event, contact, restOfInput, this)
                    return
                }
            }

            logger.warning("No command module found for prefix: $commandPrefix")
        } else {
            logger.info("Input does not start with a recognized command prefix.")
        }
    }

    fun getTranslator() = translator
}
