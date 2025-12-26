plugins {
    id("jvm")
    id("ktor-server-websocket")
}
kotlin {
}
dependencies {
    api(project(":ktcp:server"))
}
