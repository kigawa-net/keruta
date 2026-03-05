import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    kotlin("jvm")
    id("com.gradleup.shadow")
    kotlin("plugin.serialization")
    application
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        freeCompilerArgs = listOf("-Xcontext-parameters")
    }
}



application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

tasks.shadowJar {
    archiveFileName = "ktse.jar"
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

dependencies {
    implementation(project(":ktcp:server"))
    implementation(project(":ktcp:ktcp-infra"))
    // https://mvnrepository.com/artifact/com.auth0/java-jwt
    implementation("com.auth0:java-jwt:${Version.JAVA_JWT}")
    implementation("com.auth0:jwks-rsa:${Version.JWKS_RSA}")
// https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper
    implementation("org.apache.zookeeper:zookeeper:${Version.ZOOKEEPER}")
    implementation("io.ktor:ktor-server-websockets:${Version.KTOR}")
    implementation("io.ktor:ktor-server-core-jvm:${Version.KTOR}")
    implementation("io.ktor:ktor-server-netty:${Version.KTOR}")
    implementation("io.ktor:ktor-server-config-yaml:${Version.KTOR}")
    implementation("ch.qos.logback:logback-classic:${Version.LOGBACK}")
    implementation("io.ktor:ktor-server-auth:${Version.KTOR}")
    implementation("io.ktor:ktor-server-auth-jwt:${Version.KTOR}")
    implementation("io.ktor:ktor-server-content-negotiation:${Version.KTOR}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.KTOR}")
    implementation("io.ktor:ktor-server-html-builder:${Version.KTOR}")
    implementation("io.ktor:ktor-server-sessions:${Version.KTOR}")
    implementation("io.ktor:ktor-client-core:${Version.KTOR}")
    implementation("io.ktor:ktor-client-cio:${Version.KTOR}")
    implementation("io.ktor:ktor-client-content-negotiation:${Version.KTOR}")

    // Flyway and Database
    // Upgraded to 11.0.0 (V11)
    implementation("org.flywaydb:flyway-core:${Version.FLYWAY}")
    implementation("org.flywaydb:flyway-mysql:${Version.FLYWAY}")
// Source: https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:${Version.MYSQL_CONNECTOR}")
// Source: https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-core
    implementation("org.jetbrains.exposed:exposed-core:${Version.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-dao:${Version.EXPOSED}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${Version.EXPOSED}")

    // Connection Pool
    implementation("com.zaxxer:HikariCP:${Version.HIKARI_CP}")
    // コルーチンを使う場合
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:${Version.EXPOSED}")
// Source: https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-json
    implementation("org.jetbrains.exposed:exposed-json:${Version.EXPOSED}")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host:${Version.KTOR}")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:${Version.JUNIT_JUPITER}")
    testImplementation("io.mockk:mockk:${Version.MOCKK}")
    // Testcontainers for integration testing
    testImplementation("org.testcontainers:testcontainers:${Version.TESTCONTAINERS}")
    testImplementation("org.testcontainers:junit-jupiter:${Version.TESTCONTAINERS}")
    testImplementation("org.testcontainers:mysql:${Version.TESTCONTAINERS}")
    testImplementation("org.testcontainers:kafka:${Version.TESTCONTAINERS}")
    // Ktor client for WebSocket testing
    testImplementation("io.ktor:ktor-client-cio:${Version.KTOR}")
    testImplementation("io.ktor:ktor-client-websockets:${Version.KTOR}")
    // MySQL JDBC driver for Flyway tests
    testRuntimeOnly("com.mysql:mysql-connector-j:${Version.MYSQL_CONNECTOR}")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

