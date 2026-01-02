package net.kigawa.keruta.ktcl.web

import io.ktor.server.routing.*
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

interface WebRoute {
    val info: EntrypointInfo?
    fun Route.route()
}
