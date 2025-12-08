plugins {
    id("common")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":kodel:api"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}
