plugins {
    id("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin") version Version.KTOR
    id("com.gradleup.shadow")
    application
}

tasks.shadowJar {
    archiveFileName = "ktcl-k8s.jar"
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
application {
    mainClass.set("net.kigawa.keruta.ktcl.k8s.Main")
}

dependencies {
    // KTCP統合
    implementation(project(":ktcp:client"))
    implementation(project(":ktcp:ktcp-infra:ktcp-infra-client"))
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
    implementation("io.ktor:ktor-server-config-yaml:${Version.KTOR}")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.KOTLINX_SERIALIZATION}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.KOTLINX_COROUTINES}")

    // Kubernetes Java Client（最新25.x系）
    implementation("io.kubernetes:client-java:${Version.KUBERNETES_CLIENT}")
    implementation("io.kubernetes:client-java-api:${Version.KUBERNETES_CLIENT}")

    // JWT検証
    implementation("com.auth0:java-jwt:${Version.JAVA_JWT}")
    implementation("com.auth0:jwks-rsa:${Version.JWKS_RSA}")
    // Flyway and Database
    implementation("org.flywaydb:flyway-core:${Version.FLYWAY}")
    implementation("org.flywaydb:flyway-mysql:${Version.FLYWAY}")
    implementation("org.mariadb.jdbc:mariadb-java-client:${Version.MARIADB_CONNECTOR}")

    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:${Version.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-dao:${Version.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${Version.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:${Version.EXPOSED}")

    // Connection Pool
    implementation("com.zaxxer:HikariCP:${Version.HIKARI_CP}")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:${Version.JUNIT_JUPITER}")
    testImplementation("io.mockk:mockk:${Version.MOCKK}")
    testImplementation("io.ktor:ktor-server-test-host:${Version.KTOR}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
