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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.KOTLINX_SERIALIZATION}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.KOTLINX_COROUTINES}")
    implementation(project(":kodel:api"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:${Version.JUNIT_JUPITER}")
    testImplementation("io.mockk:mockk:${Version.MOCKK}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
