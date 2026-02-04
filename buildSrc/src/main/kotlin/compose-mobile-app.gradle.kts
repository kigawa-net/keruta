import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.application")
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
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["appAuthRedirectScheme"] = "net.kigawa.keruta.mobile"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":ktcl-front-mobile"))
    implementation("androidx.activity:activity-compose:1.12.3")
}