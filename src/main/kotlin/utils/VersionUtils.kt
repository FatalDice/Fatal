package uk.akane.fatal.utils

import uk.akane.fatal.BuildConstants
import java.text.SimpleDateFormat
import java.util.*

object VersionUtils {
    fun getPluginVersion(): String {
        return "宿命 Dice v${BuildConstants.MAJOR_VERSION}.${BuildConstants.HASH_VERSION}"
    }

    fun getPluginAuthor(): String {
        return BuildConstants.AUTHOR
    }

    fun getPluginVersionHeader(): String {
        return getPluginVersion() + '\n' + "by" + ' ' + getPluginAuthor()
    }

    fun getJdkVersion(): String {
        return System.getProperty("java.vm.name") ?: "Unknown JDK Version"
    }

    fun getOSName(): String {
        return getKernelName() + "/" + (System.getProperty("os.arch") ?: "Unknown architecture")
    }

    fun getKernelName(): String {
        val osName = System.getProperty("os.name").lowercase()

        return when {
            osName.contains("mac") -> "darwin"  // macOS 内核是 Darwin
            osName.contains("win") -> "windows" // Windows
            osName.contains("nix") || osName.contains("nux") -> "linux" // Linux
            else -> "unknown"
        }
    }

    fun getCompilationTime(): String {
        return SimpleDateFormat(
            "EEEE, MMMM d, yyyy h:mm:ss a z",
            Locale.ENGLISH
        ).format(BuildConstants.BUILD_TIME.toLong())
    }

    fun getOpenSourceAddress(): String {
        return "https://github.com/FatalDice/Fatal"
    }
}