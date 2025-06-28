import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.removeSuffixIfPresent
import proguard.gradle.ProGuardTask
import java.io.File

group = "uk.akane"
version = "1.0.0"

plugins {
    val kotlinVersion = "2.1.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.16.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.github.gmazzo.buildconfig") version "5.4.0"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        val proguardVersion = "7.6.0"

        classpath("com.guardsquare:proguard-gradle:$proguardVersion")
    }
}

mirai {
    noTestCore = true
    setupConsoleTestRuntime {
        classpath = classpath.filter {
            !it.nameWithoutExtension.startsWith("mirai-core-jvm")
        }
    }
}

repositories {
    if (System.getenv("CI")?.toBoolean() != true) {
        maven("https://maven.aliyun.com/repository/public")
    }
    mavenCentral()
}

dependencies {
    val overflowVersion = "1.0.6"
    val coroutineVersion = "1.10.2"
    val exposedVersion = "0.61.0"
    val sqliteJDBCVersion = "3.50.1.0"

    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:$sqliteJDBCVersion")

    compileOnly("top.mrxiaom.mirai:overflow-core-api:$overflowVersion")
    testConsoleRuntime("top.mrxiaom.mirai:overflow-core:$overflowVersion")
}

@Suppress("UnstableApiUsage")
fun String.runCommand(
    workingDir: File = File(".")
): String = providers.exec {
    setWorkingDir(workingDir)
    commandLine(split(' '))
}.standardOutput.asText.get().removeSuffixIfPresent("\n")

buildConfig {
    className("BuildConstants")
    packageName("uk.akane.fatal")

    buildConfigField("String", "AUTHOR", "\"AkaneTan, PAKTS\"")
    buildConfigField("String", "MAJOR_VERSION", "\"${project.version}\"")
    buildConfigField("String", "HASH_VERSION",  '\"' + "git rev-parse --short=7 HEAD".runCommand(workingDir = rootDir) + '\"')
    buildConfigField("String", "BUILD_TIME", "\"${System.currentTimeMillis()}\"")
}

afterEvaluate {
    tasks.shadowJar {
        enabled = true
        archiveClassifier.set("debug")
    }
}

tasks.register<ProGuardTask>("proguard") {
    dependsOn("shadowJar")

    injars(file("${buildDir}/libs/${project.name}-${project.version}-debug.jar"))
    outjars(file("${buildDir}/libs/${project.name}-${project.version}-release.jar"))

    val javaHome = System.getProperty("java.home")
    if (System.getProperty("java.version").startsWith("1.")) {
        libraryjars("$javaHome/lib/rt.jar")
    } else {
        libraryjars(mapOf("filter" to "!module-info.class"), "$javaHome/jmods/java.base.jmod")
        libraryjars(mapOf("filter" to "!module-info.class"), "$javaHome/jmods/java.sql.jmod")
    }

    addLibraryJarsFromConfiguration(project, "compileClasspath") { jar ->
        jar.name.contains("annotations") || jar.name.contains("kotlin") ||
                jar.name.contains("mirai") || jar.name.contains("exposed")
    }

    configuration("proguard-rules.pro")

    printmapping(file("${buildDir}/proguard/mapping.txt"))
    allowaccessmodification()
    repackageclasses("")
}

tasks.register("assembleRelease") {
    group = "build"
    description = "Builds the release jar with shadowJar and obfuscation"

    dependsOn("shadowJar")
    dependsOn("proguard")
}

fun ProGuardTask.addLibraryJarsFromConfiguration(
    project: Project,
    configurationName: String,
    filter: (File) -> Boolean = { true }
) {
    val configuration = project.configurations.findByName(configurationName)
    configuration?.resolve()?.filter { it.name.endsWith(".jar") && filter(it) }?.forEach { jar ->
        libraryjars(jar)
    }
}