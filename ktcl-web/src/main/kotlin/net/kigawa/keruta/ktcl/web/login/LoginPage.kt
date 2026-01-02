package net.kigawa.keruta.ktcl.web.login

import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*
import net.kigawa.keruta.ktcl.web.WebRoute
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

class LoginPage: WebRoute {

    override val info: EntrypointInfo
        get() = EntrypointInfo("login", listOf(), "")

    override fun Routing.route() {
        get("/login") {
            call.respondHtml {
                head {
                    title { +"Login" }
                }
                body {
                    h1 { +"Login" }
                    p { +"Choose your login method:" }
                    div {
                        a(href = "/auth/keycloak") { +"Login with Keycloak" }
                    }
                    br()
                    p { +"Or use basic login (for testing):" }
                    form(action = "/login", method = FormMethod.post) {
                        div {
                            label { +"Username: " }
                            textInput(name = "username")
                        }
                        div {
                            label { +"Password: " }
                            passwordInput(name = "password")
                        }
                        submitInput { value = "Login" }
                    }
                    a(href = "/") { +"Back to Home" }
                }
            }
        }
    }
}
