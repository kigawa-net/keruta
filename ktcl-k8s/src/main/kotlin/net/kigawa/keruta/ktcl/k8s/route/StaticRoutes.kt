package net.kigawa.keruta.ktcl.k8s.route

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import net.kigawa.keruta.ktcl.k8s.auth.AuthenticationHelper
import net.kigawa.keruta.ktcl.k8s.auth.UserSession
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserClaudeConfigDao
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import net.kigawa.keruta.ktcl.k8s.web.HtmlGenerator
import net.kigawa.kodel.api.log.getKogger

class StaticRoutes(
    private val authenticationHelper: AuthenticationHelper,
    private val userTokenDao: UserTokenDao,
    private val userClaudeConfigDao: UserClaudeConfigDao,
) {
    private val logger = getKogger()

    fun configure(route: Route) {
        route.apply {
            get("/") {
                logger.fine("Request to root path, checking session")
                val session = call.sessions.get<UserSession>()
                logger.fine("Session from cookie: $session")

                val user = authenticationHelper.getAuthenticatedUser(call)
                logger.fine("Authenticated user: $user")

                if (user == null) {
                    logger.fine("User not authenticated, redirecting to /login")
                    call.respondRedirect("/login")
                } else {
                    logger.fine("User authenticated: ${user.userId}")
                    val hasGithubToken = userTokenDao.getGithubToken(user.userId) != null
                    val hasClaudeToken = userClaudeConfigDao.get(user.userId) != null
                    val success = call.request.queryParameters["success"]
                    val error = call.request.queryParameters["error"]
                    call.respondText(
                        HtmlGenerator.generateIndexHtml(hasGithubToken, hasClaudeToken, success, error),
                        ContentType.Text.Html
                    )
                }
            }

            get("/health") {
                call.respondText("OK", ContentType.Text.Plain)
            }
        }
    }
}

