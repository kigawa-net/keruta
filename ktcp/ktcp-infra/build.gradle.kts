
plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":ktcp:ktcp-usecase"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
        // https://mvnrepository.com/artifact/com.auth0/java-jwt
        implementation("com.auth0:java-jwt:4.5.1")
        implementation("com.auth0:jwks-rsa:0.23.0")
        implementation("io.ktor:ktor-client-core:${Version.KTOR}")
        implementation("io.ktor:ktor-client-cio:${Version.KTOR}")
        implementation("io.ktor:ktor-client-content-negotiation:${Version.KTOR}")
        implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.KTOR}")
    }
    sourceSets["commonTest"].dependencies {
    }
    sourceSets["jvmMain"].dependencies {
    }
}
