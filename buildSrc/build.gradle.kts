plugins {
    `kotlin-dsl`
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
