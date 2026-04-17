plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

val kotlinVersion = "2.3.0"
val composeVersion = "1.10.3"
fun pluginId(pluginName: String, version: String) = "$pluginName:$pluginName.gradle.plugin:$version"
fun kotlinPluginId(pluginName: String, version: String = kotlinVersion) =
    pluginId("org.jetbrains.kotlin.$pluginName", version)

fun kotlinId(id: String) = "org.jetbrains.kotlin:$id:$kotlinVersion"
dependencies {
    implementation(kotlinPluginId("multiplatform"))
    implementation(kotlinPluginId("jvm"))
    implementation(kotlinPluginId("android"))
    implementation(kotlinPluginId("plugin.serialization"))
    implementation(kotlinPluginId("plugin.compose"))
    implementation(pluginId("com.gradleup.shadow", "9.3.2"))
    implementation(pluginId("org.jlleitschuh.gradle.ktlint", "12.1.1"))
    implementation(pluginId("com.google.devtools.ksp", "2.2.20-2.0.4"))
    implementation("org.jetbrains.compose:compose-gradle-plugin:$composeVersion")
// Source: https://mvnrepository.com/artifact/com.android.tools.build/gradle
    implementation("com.android.tools.build:gradle:9.1.0")
}
