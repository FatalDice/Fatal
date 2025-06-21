import org.jetbrains.kotlin.util.removeSuffixIfPresent

plugins {
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("com.github.gmazzo.buildconfig") version "3.1.0"
    id("net.mamoe.mirai-console") version "2.16.0"
}

group = "uk.akane"
version = "0.1.0"

repositories {
    if (System.getenv("CI")?.toBoolean() != true) {
        maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    }
    mavenCentral()
}

buildConfig {
    className("BuildConstants")
    packageName("uk.akane.fatal")
    useKotlinOutput()

    buildConfigField("String", "AUTHOR", "\"AkaneTan, PAKTS\"")
    buildConfigField("String", "MAJOR_VERSION", "\"${project.version}\"")
    buildConfigField("String", "HASH_VERSION",  '\"' + "git rev-parse --short=7 HEAD".runCommand(workingDir = rootDir) + '\"')
    buildConfigField("String", "BUILD_TIME", "\"${System.currentTimeMillis()}\"")
}

mirai {
    noTestCore = true
    setupConsoleTestRuntime {
        // 移除 mirai-core 依赖
        classpath = classpath.filter {
            !it.nameWithoutExtension.startsWith("mirai-core-jvm")
        }
    }
}

dependencies {
    val overflowVersion = "1.0.6"
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
    compileOnly("top.mrxiaom.mirai:overflow-core-api:$overflowVersion")
    testConsoleRuntime("top.mrxiaom.mirai:overflow-core:$overflowVersion")
}

fun String.runCommand(
    workingDir: File = File(".")
): String = providers.exec {
    setWorkingDir(workingDir)
    commandLine(split(' '))
}.standardOutput.asText.get().removeSuffixIfPresent("\n")