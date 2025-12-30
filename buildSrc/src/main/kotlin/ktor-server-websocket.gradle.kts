plugins {
    id("ktor-server")
}


kotlin {
}
dependencies {
    implementation("io.ktor:ktor-server-websockets:${Version.KTOR}")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
}
