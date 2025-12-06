import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
}


kotlin {
    jvm {}
    sourceSets["commonMain"].dependencies {
    }
    sourceSets["commonTest"].dependencies {
        implementation(kotlin("test"))
    }
    sourceSets["jvmMain"].dependencies {}
}
