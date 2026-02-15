package net.kigawa.keruta.ktcp.base.auth.oidc

import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import net.kigawa.keruta.ktcp.base.auth.VerifyFailErr
import net.kigawa.keruta.ktcp.base.http.HttpClient
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifyErr
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import net.kigawa.kodel.api.net.Url

class OidcConfigProvider(
    val client: HttpClient,
) {
    private val logger = getKogger()
    suspend fun get(issuer: Url): Res<OidcConf, VerifyErr> {
        logger.debug { "get oidc config from $issuer" }
        val res = client.get(issuer.plusPath("/.well-known/openid-configuration").toStrUrl())
        if (!res.status.isSuccess()) return Res.Err(VerifyFailErr("res: $res", null))
        return try {
            @Suppress("DEPRECATION")
            Res.Ok(res.body<OidcConf>())
        } catch (e: Exception) {
            Res.Err(VerifyFailErr("body: ${res.bodyAsText()}", e))
        }
    }
}
