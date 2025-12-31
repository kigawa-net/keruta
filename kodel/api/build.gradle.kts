@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}
repositories {
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-parameters")
    }
    jvm {}
    js{
        browser()
    }
    wasmJs {
        browser {
            testTask {
                val firefox = providers.gradleProperty("useFirefox")
                    .map { it.toBoolean() }
                    .getOrElse(true)
                val chrome = providers.gradleProperty("useChrome")
                    .map { it.toBoolean() }
                    .getOrElse(true)
                enabled = firefox || chrome
                useKarma {
                    if (firefox) useFirefoxHeadless()
                    if (chrome) useChromeHeadless()
                }
            }
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
