package uk.akane.fatal

import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.TestOnly
import uk.akane.fatal.components.Dispatcher
import kotlin.system.measureTimeMillis

@TestOnly
class Console {

    val logger: ConsoleLogger = ConsoleLogger()

    val dispatcher = Dispatcher(logger)
    val testEvent = ConsoleInputEvent()
    val consoleContact = ConsoleContact()

    companion object {

        const val ANSI_RESET = "\u001B[0m"
        const val ANSI_GREY = "\u001B[248m"
        const val ANSI_RED = "\u001B[31m"
        const val ANSI_PINK = "\u001B[212m"
        const val ANSI_YELLOW = "\u001B[33m"
        const val ANSI_BLUE = "\u001B[34m"

        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            val fatalTestEntry = Console()
            fatalTestEntry.dispatcher.initialize()

            println("Fatal test console\n" +
                "Type ${ANSI_YELLOW}/exit${ANSI_RESET} to exit the console.")

            while (true) {
                print("ConsoleContact >> $ANSI_YELLOW")
                val input = readLine()
                print(ANSI_RESET)
                if (input?.startsWith("/exit") == true) {
                    break
                }
                val elapsedTime = measureTimeMillis {
                    try {
                        fatalTestEntry.dispatcher.dispatch(
                            fatalTestEntry.testEvent,
                            fatalTestEntry.consoleContact,
                            fatalTestEntry.consoleContact,
                            ConsoleMessage(input ?: "")
                        )
                    } catch (_: UnsupportedOperationException) {
                        // Do nothing
                    }
                }
                fatalTestEntry.logger.info("Elapsed time: $elapsedTime ms")
            }
        }
    }
}