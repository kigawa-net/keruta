package net.kigawa.keruta.ktcl.web.auth

import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.kigawa.keruta.ktcl.web.Config
import net.kigawa.keruta.ktcl.web.WebRoute
import net.kigawa.kodel.api.entrypoint.EntrypointInfo

class KeycloakRoute(
    val config: Config,
): WebRoute {
    override val info: EntrypointInfo
        get() = EntrypointInfo("keycloak", listOf(), "Keycloak authentication route")

    override fun Route.route() {
        get {
            val baseUrl = call.request.origin.run {
                "$scheme://$serverHost${if (serverPort == 80 || serverPort == 443) "" else ":$serverPort"}"
            }
            val redirectUri = "${baseUrl}/auth/callback"
            val authorizeUrl = "${config.issuer}/realms/${config.realm}/protocol/openid-connect/auth?client_id=${config.clientId}&redirect_uri=$redirectUri&response_type=code&scope=openid"
            call.respondRedirect(authorizeUrl)
        }
    }
}
