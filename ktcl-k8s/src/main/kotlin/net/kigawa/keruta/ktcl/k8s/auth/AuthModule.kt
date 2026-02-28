package net.kigawa.keruta.ktcl.k8s.auth

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.web.UserSession

class AuthModule {

    fun configure(application: Application) {
        application.install(Sessions) {
            cookie<UserSession>("user_session") {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 3600
                // クロスドメイン対応: SameSite=NoneでSecure=true（HTTPS必須）
                cookie.extensions["SameSite"] = "None"
                cookie.secure = true
            }
            cookie<net.kigawa.keruta.ktcl.k8s.web.login.OidcSession>("oidc_session") {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 600
                cookie.extensions["SameSite"] = "None"
                cookie.secure = true
            }
        }
    }
}
