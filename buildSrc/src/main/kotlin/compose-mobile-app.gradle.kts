import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

android {
    namespace = "net.kigawa.keruta.ktcl.mobile.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "net.kigawa.keruta.ktcl.mobile"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["appAuthRedirectScheme"] = "net.kigawa.keruta.mobile"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    buildFeatures {
        compose = true
    }
}

// Configure JVM target via task - AGP 9.0+ built-in Kotlin uses tasks
afterEvaluate {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_25)
        }
    }
}
