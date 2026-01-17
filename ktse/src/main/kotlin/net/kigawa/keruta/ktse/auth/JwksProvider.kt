package net.kigawa.keruta.ktse.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import net.kigawa.keruta.ktcp.server.err.VerifyErr
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.kodel.api.err.Res
import java.net.URL

class JwksProvider {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getJwksUrl(issuer: String): Res<URL, VerifyErr> {
        val res = client.get("$issuer/.well-known/openid-configuration")
        if (!res.status.isSuccess()) return Res.Err(VerifyFailErr("res: $res", null))
        return try {
            @Suppress("DEPRECATION")
            Res.Ok(URL(res.body<OidcConf>().jwksUri))
        } catch (e: Exception) {
            Res.Err(VerifyFailErr("body: ${res.bodyAsText()}", e))
        }
    }

}
