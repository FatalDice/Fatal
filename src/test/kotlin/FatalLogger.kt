package uk.akane.fatal

import net.mamoe.mirai.utils.MiraiLogger
import org.jetbrains.annotations.TestOnly

@TestOnly
class FatalLogger(override val isEnabled: Boolean = true) : MiraiLogger {
    override val identity: String?
        get() = "DebugConsole"

    override fun debug(message: String?) {
        println("[DEBUG] $message")
    }

    override fun debug(message: String?, e: Throwable?) {
        println("[DEBUG] $message\n${e?.printStackTrace()}")
    }

    override fun error(message: String?) {
        println("[ERROR] $message")
    }

    override fun error(message: String?, e: Throwable?) {
        println("[ERROR] $message\n${e?.printStackTrace()}")
    }

    override fun info(message: String?) {
        println("[INFO] $message")
    }

    override fun info(message: String?, e: Throwable?) {
        println("[INFO] $message\n${e?.printStackTrace()}")
    }

    override fun verbose(message: String?) {
        println("[VERBOSE] $message")
    }

    override fun verbose(message: String?, e: Throwable?) {
        println("[VERBOSE] $message\n${e?.printStackTrace()}")
    }

    override fun warning(message: String?) {
        println("[WARNING] $message")
    }

    override fun warning(message: String?, e: Throwable?) {
        println("[WARNING] $message\n${e?.printStackTrace()}")
    }
}