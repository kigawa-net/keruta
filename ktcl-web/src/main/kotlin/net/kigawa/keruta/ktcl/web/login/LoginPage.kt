package net.kigawa.keruta.ktcl.web.login

import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import net.kigawa.keruta.ktcl.web.WebEntrypoints
import net.kigawa.keruta.ktcl.web.WebRoute
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.kodel.api.log.getKogger

class LoginPage(
    val webEntrypoints: WebEntrypoints,
): WebRoute {
    val logger = getKogger()

    override val info: EntrypointInfo
        get() = EntrypointInfo("login", listOf(), "")

    override fun Route.route() {
        get {
            logger.info("Routing: /login")
            call.respondHtml {
                head {
                    title { +"Login" }
                }
                body {
                    h1 { +"Login" }
                    p { +"Choose your login method:" }
                    div {
                        a(href = webEntrypoints.auth.callback.strPath) { +"Login with Keycloak" }
                    }
                    br()
                    p { +"Or use basic login (for testing):" }

                    a(href = webEntrypoints.top.strPath) { +"Back to Home" }
                }
            }
        }
    }
}
