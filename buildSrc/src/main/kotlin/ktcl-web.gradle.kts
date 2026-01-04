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
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testImplementation("io.mockk:mockk:1.13.8")
}
tasks.withType<Test> {
    useJUnitPlatform()
}

