package net.kigawa.keruta.ktcl.k8s.web.login

import io.ktor.server.routing.*

class LoginRoute {
    fun configure(route: Route) = route.get("/login") {
        val issuer = call.queryParameters["issuer"]
        val clientId = call.queryParameters["clientId"]
        TODO("oidcに従ってログイン")
    }
}
