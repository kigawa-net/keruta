plugins {
    id("jvm")
    id("ktor-server")
    id("ktor-server-websocket")
    id("serialize-jvm")
}
kotlin {
}
dependencies {
    api(project(":ktcp:client"))
    implementation("io.ktor:ktor-server-cors:${Version.KTOR}")
    // Testing
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("io.ktor:ktor-client-websockets")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testImplementation("io.mockk:mockk:1.14.7")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

