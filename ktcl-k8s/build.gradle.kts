plugins {
    id("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin") version Version.KTOR
    application
}

application {
    mainClass.set("net.kigawa.keruta.ktcl.k8s.Main")
}

dependencies {
    // KTCP統合
    implementation(project(":ktcp:client"))
    implementation(project(":kodel:api"))

    // Ktor WebSocket Client
    implementation("io.ktor:ktor-client-core:${Version.KTOR}")
    implementation("io.ktor:ktor-client-cio:${Version.KTOR}")
    implementation("io.ktor:ktor-client-websockets:${Version.KTOR}")
    implementation("io.ktor:ktor-client-content-negotiation:${Version.KTOR}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.KTOR}")

    // Ktor Server
    implementation("io.ktor:ktor-server-core:${Version.KTOR}")
    implementation("io.ktor:ktor-server-netty:${Version.KTOR}")
    implementation("io.ktor:ktor-server-sessions:${Version.KTOR}")
    implementation("io.ktor:ktor-server-auth:${Version.KTOR}")
    implementation("io.ktor:ktor-server-content-negotiation:${Version.KTOR}")
    implementation("io.ktor:ktor-server-cors:${Version.KTOR}")
    implementation("io.ktor:ktor-server-status-pages:${Version.KTOR}")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    // Kubernetes Java Client（最新25.x系）
    implementation("io.kubernetes:client-java:25.0.0")
    implementation("io.kubernetes:client-java-api:25.0.0")

    // JWT検証
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("com.auth0:jwks-rsa:0.22.1")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testImplementation("io.mockk:mockk:1.14.7")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
