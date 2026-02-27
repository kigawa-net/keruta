plugins {
    kotlin("jvm")
}


repositories {
    mavenCentral()
    gradlePluginPortal()
}
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-parameters")
    }
}
