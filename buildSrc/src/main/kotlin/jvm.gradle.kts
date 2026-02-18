plugins {
    kotlin("jvm")
}


repositories {
    mavenCentral()
    gradlePluginPortal()
}
kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-parameters")
    }
}
