plugins {
    id("jvm")
    id("ktor-server-websocket")
}
kotlin {
}
dependencies {
    api(project(":ktcp:server"))
    // https://mvnrepository.com/artifact/com.auth0/java-jwt
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("com.auth0:jwks-rsa:0.23.0")
// https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper
    implementation("org.apache.zookeeper:zookeeper:3.9.4")

    // Flyway and Database
    // https://mvnrepository.com/artifact/org.flywaydb/flyway-core
    implementation("org.flywaydb:flyway-core:10.20.1")
    // https://mvnrepository.com/artifact/org.flywaydb/flyway-mysql
    implementation("org.flywaydb:flyway-mysql:10.20.1")
    // https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    implementation("com.mysql:mysql-connector-j:9.0.0")
    implementation("org.jetbrains.exposed:exposed-core:0.47.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.47.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.47.0")

    // コルーチンを使う場合
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.61.0")
// Source: https://mvnrepository.com/artifact/org.jetbrains.exposed/exposed-json
    implementation("org.jetbrains.exposed:exposed-json:0.61.0")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testImplementation("io.mockk:mockk:1.14.7")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

