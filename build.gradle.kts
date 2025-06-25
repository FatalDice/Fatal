import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.removeSuffixIfPresent
import java.io.File

group = "uk.akane"
version = "0.1.0"

plugins {
    val kotlinVersion = "2.1.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.16.0"
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

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:$sqliteJDBCVersion")

    compileOnly("top.mrxiaom.mirai:overflow-core-api:$overflowVersion")
    testConsoleRuntime("top.mrxiaom.mirai:overflow-core:$overflowVersion")
}

open class BuildConfigExtension {
    private var _className: String = "BuildConfig"
    private var _packageName: String = ""
    var className: String
        set(value) { _className = value }
        get() = _className
    var packageName: String
        set(value) { _packageName = value }
        get() = _packageName
    fun BuildConfigExtension.className(className: String) { this.className = className }
    fun BuildConfigExtension.packageName(packageName: String) { this.packageName = packageName }

    val fields: MutableList<String> = mutableListOf()

    fun buildConfigField(type: String, name: String, value: String) {
        fields.add("    const val $name: $type = $value")
    }

    fun generateBuildConstantsFile(): String {
        val content = StringBuilder()
        content.appendLine("package $packageName")
        content.appendLine()
        content.appendLine("object $className {")
        fields.forEach { content.appendLine(it) }
        content.appendLine("}")
        return content.toString()
    }

    fun generateBuildConstants(outputDir: File) {
        val generatedClassContent = generateBuildConstantsFile()
        outputDir.mkdirs()
        val file = File(outputDir, "${className}.kt")
        file.writeText(generatedClassContent)
    }
}

fun Project.buildConfig(configure: BuildConfigExtension.() -> Unit) {
    val extension = extensions.create<BuildConfigExtension>("buildConfig")
    extension.configure()

    val outputDir = File("${buildDir}/generated/sources")
    extension.generateBuildConstants(outputDir)
}

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

tasks.withType<KotlinCompile> {
    source("${buildDir}/generated/sources")
}
