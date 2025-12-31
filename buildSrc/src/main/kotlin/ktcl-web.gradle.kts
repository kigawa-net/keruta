import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("kmp")
    // KSP support needed for Lens generation
    id("com.google.devtools.ksp")
}
kotlin {
    js(IR) {
        browser()
    }.binaries.executable()
    sourceSets {
        commonMain {
            dependencies {
                api(project(":ktcp:client"))
                implementation("dev.fritz2:core:${Version.FRITZ2}")
            }
        }
    }
}
// KSP support for Lens generation
dependencies {
    add("kspCommonMainMetadata", "dev.fritz2:lenses-annotation-processor:${Version.FRITZ2}")
}

project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
