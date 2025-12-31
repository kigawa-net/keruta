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
    js {
        browser {}
    }
    sourceSets["commonMain"].dependencies {
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}
