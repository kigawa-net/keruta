import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    id("io.ktor.plugin") version Version.KTOR
    kotlin("jvm")

    kotlin("plugin.serialization")
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

//tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
//    mergeServiceFiles()
//    archiveClassifier.set("all")
//
//    // リソースの重複やマージに関する問題を防ぐための明示的な設定（必要に応じて）
//    append("META-INF/services/org.flywaydb.core.internal.scanner.ScannerCustomizer")
//}

dependencies {
    api(project(":ktcp:server"))
    api(project(":ktcp:ktcp-infra"))
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
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-sessions")
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
    testImplementation("io.ktor:ktor-server-test-host")
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

