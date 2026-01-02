package net.kigawa.keruta.ktcl.web.auth

import io.ktor.http.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import net.kigawa.keruta.ktcl.web.WebRoute
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

class CallbackRoute: WebRoute {
    override val info: EntrypointInfo?
        get() = EntrypointInfo("callback", listOf(), "")

    override fun Route.route() {
        get {
            val code = call.request.queryParameters["code"]
            if (code != null) {
                // Here you would exchange the code for access token
                // For now, just show success
                call.respondHtml {
                    head {
                        title { +"Login Success" }
                    }
                    body {
                        h1 { +"Login Successful!" }
                        p { +"Authorization Code: $code" }
                        a(href = "/") { +"Go to Home" }
                    }
                }
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Authentication failed")
            }
        }
    }
}
