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

    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testImplementation("io.mockk:mockk:1.14.7")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

