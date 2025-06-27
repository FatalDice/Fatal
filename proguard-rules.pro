-keep class uk.akane.fatal.FatalPlugin { *; }
-keep class uk.akane.fatal.data.** { *; }

-keep class net.mamoe.mirai.console.plugin.jvm.KotlinPlugin { *; }
-keep class net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription { *; }

-keep class net.mamoe.mirai.** { *; }
-dontwarn net.mamoe.mirai.**

-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keep class org.jetbrains.kotlin.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**
-dontwarn org.jetbrains.kotlin.**

-keep class org.sqlite.** { *; }
-dontwarn org.sqlite.**

-dontwarn java.util.logging.**

-keep class org.jetbrains.exposed.** { *; }
-dontwarn org.jetbrains.exposed.**

-keep class java.sql.** { *; }
-dontwarn java.sql.**

-keep class javax.sql.** { *; }
-dontwarn javax.sql.**

-dontnote