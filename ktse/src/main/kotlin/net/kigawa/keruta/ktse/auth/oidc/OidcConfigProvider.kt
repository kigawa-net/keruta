package net.kigawa.keruta.ktse.auth.oidc

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.keruta.ktse.http.HttpClient
import net.kigawa.kodel.api.err.Res

class OidcConfigProvider(
    val client: HttpClient,
) {

    suspend fun get(issuer: String): Res<OidcConf, VerifyErr> {
        val res = client.get("$issuer/.well-known/openid-configuration")
        if (!res.status.isSuccess()) return Res.Err(VerifyFailErr("res: $res", null))
        return try {
            @Suppress("DEPRECATION")
            Res.Ok(res.body<OidcConf>())
        } catch (e: Exception) {
            Res.Err(VerifyFailErr("body: ${res.bodyAsText()}", e))
        }
    }
}
