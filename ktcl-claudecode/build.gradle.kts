plugins {
    id("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin") version Version.KTOR
    application
}

application {
    mainClass.set("net.kigawa.keruta.ktcl.claudecode.KerutaClaudeCodeClientKt")
}

dependencies {
    implementation(project(":ktcp:client"))
    implementation(project(":ktcp:ktcp-infra:ktcp-infra-client"))
    implementation("io.ktor:ktor-client-core:${Version.KTOR}")
    implementation("io.ktor:ktor-client-cio:${Version.KTOR}")
    implementation("io.ktor:ktor-client-websockets:${Version.KTOR}")
    implementation("io.ktor:ktor-client-content-negotiation:${Version.KTOR}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.KTOR}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation(project(":kodel:api"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testImplementation("io.mockk:mockk:1.14.7")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
