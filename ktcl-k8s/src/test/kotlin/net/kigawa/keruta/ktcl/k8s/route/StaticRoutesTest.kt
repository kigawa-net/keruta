package net.kigawa.keruta.ktcl.k8s.route

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.mockk
import net.kigawa.keruta.ktcl.k8s.auth.AuthenticationHelper
import net.kigawa.keruta.ktcl.k8s.login.ProviderListClient
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserClaudeConfigDao
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import kotlin.test.Test
import kotlin.test.assertEquals

class StaticRoutesTest {
    private val staticRoutes = StaticRoutes(
        mockk<AuthenticationHelper>(),
        mockk<UserTokenDao>(),
        mockk<UserClaudeConfigDao>(),
        mockk<ProviderListClient>(),
    )

    @Test
    fun `healthエンドポイントは200 OKを返す`() = testApplication {
        application {
            routing {
                staticRoutes.configure(this)
            }
        }
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OK", response.bodyAsText())
    }
}
