
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
}
