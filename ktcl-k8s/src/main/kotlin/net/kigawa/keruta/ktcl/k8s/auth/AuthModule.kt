package net.kigawa.keruta.ktcl.k8s.auth

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.web.UserSession

class AuthModule {

    fun configure(application: Application) {
        // 環境変数からSameSite設定を読み取り（デフォルトはLax）
        val sameSite = System.getenv("KTC_K8S_SAME_SITE") ?: "Lax"
        val secure = System.getenv("KTC_K8S_COOKIE_SECURE")?.lowercase() == "true"

        application.install(Sessions) {
            cookie<UserSession>("user_session") {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 3600
                cookie.extensions["SameSite"] = sameSite
                if (secure) cookie.secure = true
            }
            cookie<net.kigawa.keruta.ktcl.k8s.web.login.OidcSession>("oidc_session") {
                cookie.path = "/"
                cookie.maxAgeInSeconds = 600
                cookie.extensions["SameSite"] = sameSite
                if (secure) cookie.secure = true
            }
        }
    }
}
