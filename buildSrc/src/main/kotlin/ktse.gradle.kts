plugins {
    id("jvm")
    id("ktor-server-websocket")

    kotlin("plugin.serialization")
}
kotlin {
}
dependencies {
    api(project(":ktcp:server"))
    api(project(":ktcp:base"))
    // https://mvnrepository.com/artifact/com.auth0/java-jwt
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("com.auth0:jwks-rsa:0.23.0")
// https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper
    implementation("org.apache.zookeeper:zookeeper:3.9.4")

    // Flyway and Database
    // https://mvnrepository.com/artifact/org.flywaydb/flyway-core
    // Downgraded to 8.5.13 due to ShadowJAR resource scanning issues in 9.x, 10.x and 11.x
    implementation("org.flywaydb:flyway-core:8.5.13")
    implementation("org.flywaydb:flyway-mysql:8.5.13")
// Source: https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:9.6.0")
// Source: https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-core
    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")

    // Connection Pool
    implementation("com.zaxxer:HikariCP:7.0.2")
    // コルーチンを使う場合
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.61.0")
// Source: https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-json
    implementation("org.jetbrains.exposed:exposed-json:0.61.0")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testImplementation("io.mockk:mockk:1.14.7")
    // MySQL JDBC driver for Flyway tests
    testRuntimeOnly("com.mysql:mysql-connector-j:9.6.0")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

