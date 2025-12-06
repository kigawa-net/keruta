import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("common")
}
allprojects {
    group = "net.kigawa.keruta"
    version = System.getenv("VERSION") ?: "dev"
    apply(plugin = "common")
}

