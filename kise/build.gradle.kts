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
        implementation("io.ktor:ktor-server-websockets-jvm:${Version.KTOR}")
        implementation("io.ktor:ktor-server-core-jvm:${Version.KTOR}")
        implementation("io.ktor:ktor-server-netty-jvm:${Version.KTOR}")
        implementation("io.ktor:ktor-server-sessions-jvm:${Version.KTOR}")
        implementation("io.ktor:ktor-server-auth-jvm:${Version.KTOR}")
        implementation("io.ktor:ktor-server-content-negotiation-jvm:${Version.KTOR}")
        implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:${Version.KTOR}")
        implementation("io.ktor:ktor-client-core-jvm:${Version.KTOR}")
        implementation("io.ktor:ktor-client-cio-jvm:${Version.KTOR}")
        implementation("io.ktor:ktor-client-content-negotiation-jvm:${Version.KTOR}")
        implementation("com.auth0:java-jwt:4.5.2")
        implementation("com.auth0:jwks-rsa:${Version.JWKS_RSA}")
        api(project(":ktcp-sdk:ktcp-domain:ktcp-domain-server"))
        api(project(":ktcp-sdk:ktcp-domain"))
        // Database
        implementation("org.jetbrains.exposed:exposed-core:${Version.EXPOSED}")
        implementation("org.jetbrains.exposed:exposed-dao:${Version.EXPOSED}")
        implementation("org.jetbrains.exposed:exposed-jdbc:${Version.EXPOSED}")
        implementation("org.jetbrains.exposed:exposed-kotlin-datetime:${Version.EXPOSED}")
        // Auth dependencies - for Auth0JwtVerifier in ktcp-sdk/src/jvmMain
        api(project(":ktcp-sdk"))
        // For UnverifiedAuthTokens, AuthTokenDecoder interface
        api(project(":ktse-sdk"))
        implementation("com.mysql:mysql-connector-j:9.7.0")
        implementation("com.zaxxer:HikariCP:7.0.2")
    }
    sourceSets["jvmTest"].dependencies {
        implementation("io.ktor:ktor-server-test-host-jvm:${Version.KTOR}")
        implementation("org.jetbrains.kotlin:kotlin-test")
        implementation("org.junit.jupiter:junit-jupiter:${Version.JUNIT_JUPITER}")
        implementation("io.mockk:mockk:${Version.MOCKK}")
    }
}