plugins {
    id("common")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        implementation(project(":kodel:api"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}
