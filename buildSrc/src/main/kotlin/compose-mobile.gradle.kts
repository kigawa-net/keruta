import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("kmp")
    id("serialize")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.library")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

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
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            // TODO: Re-enable when iOS compatibility is resolved
            // implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
            // implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
            implementation("io.ktor:ktor-client-core:${Version.KTOR}")
            implementation("io.ktor:ktor-client-content-negotiation:${Version.KTOR}")
            implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.KTOR}")
            implementation(project(":ktcp:client"))
            implementation(project(":kodel:api"))
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation("androidx.activity:activity-compose:1.10.0")
            implementation("io.ktor:ktor-client-okhttp:${Version.KTOR}")
            implementation("androidx.security:security-crypto:1.1.0")
            implementation("net.openid:appauth:0.11.1")
        }

        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:${Version.KTOR}")
        }
    }
}

android {
    namespace = "net.kigawa.keruta.ktcl.mobile"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
compose {

}
