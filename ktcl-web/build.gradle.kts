plugins {
    id("ktcl-web")
    id("io.ktor.plugin") version Version.KTOR
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}
