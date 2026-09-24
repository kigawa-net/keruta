package net.kigawa.keruta.ktse.kicp

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.err.PeerVerificationErr
import net.kigawa.keruta.kicp.domain.identity.RegisterId
import net.kigawa.keruta.kicp.domain.repo.PeerServerClient
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.kodel.api.err.Res

/**
 * 対端サーバー（idServerA）に登録トークンを検証する実装
 * 実際の環境では、idServerAのAPIエンドポイントを呼び出す
 */
class PeerServerClientImpl(
    private val httpClient: HttpClient,
    private val peerServerBaseUrl: String,
) : PeerServerClient {
    override suspend fun verifyRegister(registerId: RegisterId, registerToken: RegisterToken): Res<Unit, KicpErr> {
        return try {
            // idServerAの検証エンドポイントを呼び出す
            val response: HttpResponse = httpClient.post("$peerServerBaseUrl/api/kicp/verify-register") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "registerId" to registerId.value,
                        "registerToken" to registerToken.value
                    )
                )
            }
            
            if (response.status == HttpStatusCode.OK) {
                Res.Ok(Unit)
            } else {
                Res.Err(PeerVerificationErr("登録トークンの検証に失敗しました: ${response.status}"))
            }
        } catch (e: Exception) {
            Res.Err(PeerVerificationErr("対端サーバーとの通信に失敗しました: ${e.message}", e))
        }
    }
}
