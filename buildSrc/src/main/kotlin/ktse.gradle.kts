plugins {
    id("jvm")
    id("ktor-server")
}
kotlin {
}
dependencies {
    api(project(":ktcp:server"))
}
