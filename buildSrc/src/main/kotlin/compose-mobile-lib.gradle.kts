import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("plugin.compose")
    id("kmp")
    id("serialize")
    id("org.jetbrains.compose")
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.compose.hot-reload")
}

kotlin {
    // iOS targets are configured by kmp plugin
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    androidLibrary {
        namespace = "net.kigawa.keruta.ktcl.mobile"
        compileSdk = 36

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_25)
        }

        minSdk = 26

    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:1.10.3")
            implementation("org.jetbrains.compose.foundation:foundation:1.10.2")
            implementation("org.jetbrains.compose.material3:material3:1.9.0")
            implementation("org.jetbrains.compose.ui:ui:1.10.3")
            implementation("org.jetbrains.compose.components:components-resources:1.10.3")
            // TODO: Re-enable when iOS compatibility is resolved
            // implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
            // implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("io.ktor:ktor-client-core:${Version.KTOR}")
            implementation("io.ktor:ktor-client-content-negotiation:${Version.KTOR}")
            implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.KTOR}")
            implementation(project(":ktcp-sdk:client"))
            implementation(project(":kodel:api"))
        }

        androidMain.dependencies {
            implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.3")
            implementation("io.ktor:ktor-client-okhttp:${Version.KTOR}")
            implementation("androidx.security:security-crypto:1.1.0")
            implementation("net.openid:appauth:0.11.1")
        }

        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:${Version.KTOR}")
        }
    }
}
