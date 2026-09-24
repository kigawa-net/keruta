import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("ktlint")
}


repositories {
    mavenCentral()
    gradlePluginPortal()
}
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        freeCompilerArgs = listOf("-Xcontext-parameters")
    }
}
