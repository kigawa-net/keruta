plugins {
    kotlin("multiplatform")
}
repositories {
    mavenCentral()
    gradlePluginPortal()
}
allprojects {
    group = "net.kigawa.kinfra"
    version = System.getenv("VERSION") ?: "dev"
    apply(plugin = "common")
}

