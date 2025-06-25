package uk.akane.fatal

import net.mamoe.mirai.utils.MiraiLogger
import org.jetbrains.annotations.TestOnly

@TestOnly
class ConsoleLogger(override val isEnabled: Boolean = true) : MiraiLogger {
    override val identity: String?
        get() = "DebugConsole"

    override fun debug(message: String?) {
        println("${Console.ANSI_GREEN}[DEBUG]${Console.ANSI_RESET} $message")
    }

    override fun debug(message: String?, e: Throwable?) {
        println("${Console.ANSI_GREEN}[DEBUG]${Console.ANSI_RESET} $message\n${e?.printStackTrace()}")
    }

    override fun error(message: String?) {
        println("${Console.ANSI_RED}[ERROR]${Console.ANSI_RESET} $message")
    }

    override fun error(message: String?, e: Throwable?) {
        println("${Console.ANSI_RED}[ERROR]${Console.ANSI_RESET} $message\n${e?.printStackTrace()}")
    }

    override fun info(message: String?) {
        println("${Console.ANSI_BLUE}[INFO]${Console.ANSI_RESET} $message")
    }

    override fun info(message: String?, e: Throwable?) {
        println("${Console.ANSI_BLUE}[INFO]${Console.ANSI_RESET} $message\n${e?.printStackTrace()}")
    }

    override fun verbose(message: String?) {
        println("${Console.ANSI_YELLOW}[VERBOSE]${Console.ANSI_RESET} $message")
    }

    override fun verbose(message: String?, e: Throwable?) {
        println("${Console.ANSI_YELLOW}[VERBOSE]${Console.ANSI_RESET} $message\n${e?.printStackTrace()}")
    }

    override fun warning(message: String?) {
        println("${Console.ANSI_PINK}[WARNING]${Console.ANSI_RESET} $message")
    }

    override fun warning(message: String?, e: Throwable?) {
        println("${Console.ANSI_PINK}[WARNING]${Console.ANSI_RESET} $message\n${e?.printStackTrace()}")
    }
}