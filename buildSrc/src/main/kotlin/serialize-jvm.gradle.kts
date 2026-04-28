plugins {
    id("jvm")
    kotlin("plugin.serialization")
}
kotlin {
}
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.KOTLINX_SERIALIZATION}")
}
