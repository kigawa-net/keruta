plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version "2.2.21"
}

kotlin {
    compilerOptions {
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
}



repositories {
    mavenCentral()
    gradlePluginPortal()
}

val kotlinVersion = "2.2.21"
fun pluginId(pluginName: String, version: String) = "$pluginName:$pluginName.gradle.plugin:$version"
fun kotlinPluginId(pluginName: String, version: String = kotlinVersion) =
    pluginId("org.jetbrains.kotlin.$pluginName", version)

fun kotlinId(id: String) = "org.jetbrains.kotlin:$id:$kotlinVersion"
dependencies {
    implementation(kotlinPluginId("multiplatform"))
    implementation(kotlinPluginId("jvm"))
    implementation(kotlinPluginId("plugin.serialization"))
    implementation(pluginId("com.gradleup.shadow", "9.3.0"))
    implementation(pluginId("org.jlleitschuh.gradle.ktlint", "12.1.1"))

}
