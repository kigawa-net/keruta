package net.kigawa.keruta.ktcl.k8s.web

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.kigawa.kodel.api.log.LoggerFactory

private val logger = LoggerFactory.get("WebServer")

/**
 * Ktor Webサーバーを起動する（ブロッキング）
 */
fun startWebServer(port: Int = 8081) {
    logger.info("Starting web server on port $port")

    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * Ktor Webサーバーを起動する（非ブロッキング）
 */
fun startWebServerNonBlocking(port: Int = 8081) {
    logger.info("Starting web server (non-blocking) on port $port")

    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = false)
}
