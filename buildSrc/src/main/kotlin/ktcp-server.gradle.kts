plugins {
    id("common")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":ktcp:model"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}
