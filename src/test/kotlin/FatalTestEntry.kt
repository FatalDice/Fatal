package uk.akane.fatal

import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.TestOnly
import uk.akane.fatal.components.Dispatcher
import kotlin.system.measureTimeMillis

@TestOnly
class FatalTestEntry {

    private val logger: FatalLogger = FatalLogger()

    val dispatcher = Dispatcher(logger)
    val testEvent = ConsoleInputEvent()
    val consoleContact = ConsoleContact()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            val fatalTestEntry = FatalTestEntry()
            fatalTestEntry.dispatcher.initialize()

            println("Fatal test console\n" +
                "Type /exit to exit the console.")

            var readTime = 0L

            while (true) {
                try {
                    print("ConsoleContact >> ")
                    val input = readLine()
                    readTime = System.currentTimeMillis()
                    if (input == "exit") {
                        return@runBlocking
                    }
                    val elapsedTime = measureTimeMillis {
                        fatalTestEntry.dispatcher.dispatch(
                            fatalTestEntry.testEvent,
                            fatalTestEntry.consoleContact,
                            ConsoleMessage(input ?: "")
                        )
                    }
                } catch (_: UnsupportedOperationException) {
                    println("Elapsed time: ${System.currentTimeMillis() - readTime} ms")
                }
            }
        }
    }
}