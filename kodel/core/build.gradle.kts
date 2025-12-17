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
    sourceSets["commonMain"].dependencies {
        implementation(project(":kodel:api"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    }
    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    }
    sourceSets["jvmMain"].dependencies {}
}
