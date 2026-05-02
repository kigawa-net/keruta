plugins {
    id("kmp")
    id("serialize")
}

kotlin {
    jvm()
    sourceSets["commonMain"].dependencies {
        api(project(":kise:domain"))
        api(project(":kise:usecase"))
        api(project(":kodel:api"))
        api(project(":kodel:coroutine"))
    }
    sourceSets["jvmMain"].dependencies {
        implementation("io.ktor:ktor-server-websockets-jvm:3.4.3")
        implementation("io.ktor:ktor-server-core-jvm:3.4.3")
        implementation("com.auth0:java-jwt:4.5.2")
        api(project(":ktcp-sdk:ktcp-domain:ktcp-domain-server"))
        api(project(":ktcp-sdk:ktcp-domain"))
    }
}