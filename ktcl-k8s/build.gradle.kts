plugins {
    id("jvm")
    kotlin("plugin.serialization")
    application
}

application {
    mainClass.set("net.kigawa.keruta.ktcl.k8s.KerutaK8sClientKt")
}

dependencies {
    // KTCP統合
    implementation(project(":ktcp:client"))
    implementation(project(":kodel:api"))

    // Ktor WebSocket Client
    implementation("io.ktor:ktor-client-core:${Version.KTOR}")
    implementation("io.ktor:ktor-client-cio:${Version.KTOR}")
    implementation("io.ktor:ktor-client-websockets:${Version.KTOR}")
    implementation("io.ktor:ktor-client-content-negotiation:${Version.KTOR}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Version.KTOR}")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    // Kubernetes Java Client（最新25.x系）
    implementation("io.kubernetes:client-java:25.0.0")
    implementation("io.kubernetes:client-java-api:25.0.0")

    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testImplementation("io.mockk:mockk:1.14.7")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
