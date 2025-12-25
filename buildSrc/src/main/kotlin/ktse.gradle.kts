plugins {
    id("jvm")
    id("ktor-server-websocket")
}
kotlin {
}
dependencies {
    api(project(":ktcp:server"))
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.+")
}
