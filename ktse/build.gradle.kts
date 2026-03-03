
plugins {
    id("io.ktor.plugin") version Version.KTOR
    id("jvm")
    id("ktor-server-websocket")

    kotlin("plugin.serialization")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    mergeServiceFiles()
    archiveClassifier.set("all")
}
kotlin {
}
dependencies {
    api(project(":ktcp:server"))
    api(project(":ktcp:ktcp-infra"))
    // https://mvnrepository.com/artifact/com.auth0/java-jwt
    implementation("com.auth0:java-jwt:4.5.1")
    implementation("com.auth0:jwks-rsa:0.23.0")
// https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper
    implementation("org.apache.zookeeper:zookeeper:3.9.4")

    // Flyway and Database
    // https://mvnrepository.com/artifact/org.flywaydb/flyway-core
    // Downgraded to 9.22.3 due to location prefix issues in 11.x and 12.x
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("org.flywaydb:flyway-mysql:9.22.3")
// Source: https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:9.6.0")
// Source: https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-core
    implementation("org.jetbrains.exposed:exposed-core:0.60.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.60.0")

    // Connection Pool
    implementation("com.zaxxer:HikariCP:7.0.2")
    // コルーチンを使う場合
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.60.0")
// Source: https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-json
    implementation("org.jetbrains.exposed:exposed-json:0.60.0")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
    testImplementation("io.mockk:mockk:1.14.9")
    // Testcontainers for integration testing
    testImplementation("org.testcontainers:testcontainers:1.20.4")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testImplementation("org.testcontainers:mysql:1.20.4")
    testImplementation("org.testcontainers:kafka:1.20.4")
    // Ktor client for WebSocket testing
    testImplementation("io.ktor:ktor-client-cio:3.4.0")
    testImplementation("io.ktor:ktor-client-websockets:3.4.0")
    // MySQL JDBC driver for Flyway tests
    testRuntimeOnly("com.mysql:mysql-connector-j:9.6.0")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

