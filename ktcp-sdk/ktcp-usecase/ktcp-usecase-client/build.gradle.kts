plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":ktcp-sdk:ktcp-domain:ktcp-domain-client"))
        api(project(":ktcp-sdk:ktcp-usecase"))
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {}
}
