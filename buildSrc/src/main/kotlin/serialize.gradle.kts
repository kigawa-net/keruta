plugins {
    id("kmp")
    kotlin("plugin.serialization")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.KOTLINX_SERIALIZATION}")
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}
