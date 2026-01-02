package net.kigawa.keruta.ktcl.web

import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

class TopPage: WebRoute {
    override val info: EntrypointInfo?
        get() = null

    override fun Route.route() {
        get("/") {
            call.respondHtml {
                head {
                    title { +"Keruta Task Client Web" }
                }
                body {
                    h1 { +"Welcome to Keruta Task Client Web" }
                    p { +"Please log in to access the system." }
                    a(href = "/login") { +"Go to Login Page" }
                }
            }
        }
    }
}
