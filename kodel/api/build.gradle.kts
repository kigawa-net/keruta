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
    }
    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test"))
    }
    sourceSets["jvmMain"].dependencies {}
}
