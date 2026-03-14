
plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":ktcp-sdk:ktcp-usecase:ktcp-usecase-server"))
        api(project(":ktcp-sdk"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {
    }
}
