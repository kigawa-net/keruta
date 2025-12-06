plugins {
    `kotlin-dsl`
}

repositories {
    // Need the Gradle Plugin Portal for plugin marker artifacts like
    // io.github.gradle-nexus.publish-plugin.gradle.plugin
    gradlePluginPortal()
    mavenCentral()
}
val kotlinVersion = "2.2.0"
fun pluginId(pluginName: String, version: String) = "$pluginName:$pluginName.gradle.plugin:$version"
fun kotlinPluginId(pluginName: String, version: String = kotlinVersion) =
    pluginId("org.jetbrains.kotlin.$pluginName", kotlinVersion)

fun kotlinId(id: String) = "org.jetbrains.kotlin:$id:$kotlinVersion"
dependencies {
    implementation(kotlinPluginId("jvm"))
    implementation(kotlinPluginId("plugin.serialization"))
    implementation(pluginId("io.github.gradle-nexus.publish-plugin","2.0.0"))
}
