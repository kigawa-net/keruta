
plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":ktcp-sdk:ktcp-usecase:ktcp-usecase-client"))
        api(project(":ktcp-sdk"))
// Source: https://mvnrepository.com/artifact/com.nimbusds/nimbus-jose-jwt
        implementation("com.nimbusds:nimbus-jose-jwt:10.8")
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {
    }
}
