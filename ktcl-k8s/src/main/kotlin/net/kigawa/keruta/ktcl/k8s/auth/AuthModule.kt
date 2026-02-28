package net.kigawa.keruta.ktcl.k8s.auth

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.web.UserSession

class AuthModule {

    fun configure(application: Application) {
        application.install(Sessions) {
            cookie<UserSession>("user_session", SessionStorageMemory()) {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 3600
                cookie.httpOnly = false
                cookie.extensions["SameSite"] = "Lax"
            }
            cookie<net.kigawa.keruta.ktcl.k8s.web.login.OidcSession>("oidc_session", SessionStorageMemory()) {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 600
                cookie.httpOnly = false
                cookie.extensions["SameSite"] = "Lax"
            }
        }
    }
}
