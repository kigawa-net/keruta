package net.kigawa.keruta.ktse.auth.keruta

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.server.err.VerifyFailErr
import net.kigawa.keruta.ktse.http.HttpClient
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import net.kigawa.kodel.api.net.Url

class KerutaJsonProvider(
    val client: HttpClient,
) {
    private val logger = getKogger()

    suspend fun get(issuer: Url): Res<KerutaJson, KtcpErr> {
        logger.debug { "get keruta.json from $issuer" }
        val res = client.get(issuer.plusPath("/.well-known/keruta.json").toStrUrl())
        if (!res.status.isSuccess()) return Res.Err(VerifyFailErr("res: $res", null))
        return try {
            @Suppress("DEPRECATION")
            Res.Ok(res.body<KerutaJson>())
        } catch (e: Exception) {
            Res.Err(VerifyFailErr("body: ${res.bodyAsText()}", e))
        }
    }
}
