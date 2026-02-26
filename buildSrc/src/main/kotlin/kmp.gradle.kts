import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

plugins {
    kotlin("multiplatform")
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}
fun KotlinJsTest.browserTest() {
    val firefox = providers.gradleProperty("useFirefox")
        .map { it.toBoolean() }
        .getOrElse(true)
    val chrome = providers.gradleProperty("useChrome")
        .map { it.toBoolean() }
        .getOrElse(true)
    enabled = firefox || chrome
    useKarma {
        if (firefox) useFirefoxHeadless()
        if (chrome) useChromeHeadlessNoSandbox()
    }
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-parameters")
    }
    jvm {}
    js {
        browser {
            testTask {
                browserTest()
            }
        }
        nodejs {
            testTask {
                browserTest()
            }
        }
    }

    // iOS targets for mobile support
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { _ ->

    }
    sourceSets {
        commonMain {}
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
