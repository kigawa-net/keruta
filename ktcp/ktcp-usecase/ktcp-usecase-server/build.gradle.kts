plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":ktcp:ktcp-domain:ktcp-domain-server"))
        api(project(":ktcp:ktcp-usecase"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}
