
plugins {
    id("kmp")
    id("serialize")
}
kotlin {
    sourceSets["commonMain"].dependencies {
        api(project(":ktcp:ktcp-usecase"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.KOTLINX_SERIALIZATION}")
        // https://mvnrepository.com/artifact/com.auth0/java-jwt
        implementation("com.auth0:java-jwt:${Version.JAVA_JWT}")
        implementation("com.auth0:jwks-rsa:${Version.JWKS_RSA}")
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
