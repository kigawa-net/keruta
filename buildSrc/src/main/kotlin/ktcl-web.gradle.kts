plugins {
    id("jvm")
    id("ktor-server")
}
kotlin {
}
dependencies {
    api(project(":ktcp:client"))
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

