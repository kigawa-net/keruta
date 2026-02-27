
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
    // Downgraded to 8.5.13 due to ShadowJAR resource scanning issues in 9.x, 10.x and 11.x
    implementation("org.flywaydb:flyway-core:12.0.3")
    implementation("org.flywaydb:flyway-mysql:12.0.3")
// Source: https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:9.6.0")
// Source: https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-core
    implementation("org.jetbrains.exposed:exposed-core:1.1.1")
    implementation("org.jetbrains.exposed:exposed-dao:1.1.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.1.1")

    // Connection Pool
    implementation("com.zaxxer:HikariCP:7.0.2")
    // コルーチンを使う場合
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:1.1.1")
// Source: https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-json
    implementation("org.jetbrains.exposed:exposed-json:1.1.1")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
    testImplementation("io.mockk:mockk:1.14.9")
    // MySQL JDBC driver for Flyway tests
    testRuntimeOnly("com.mysql:mysql-connector-j:9.6.0")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

