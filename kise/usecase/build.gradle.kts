plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    jvm()
    sourceSets["commonMain"].dependencies {
        api(project(":kise:domain"))
        api(project(":kodel:api"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}
