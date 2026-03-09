
plugins {
    id("jvm")
}


kotlin {
}
dependencies {
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
}
