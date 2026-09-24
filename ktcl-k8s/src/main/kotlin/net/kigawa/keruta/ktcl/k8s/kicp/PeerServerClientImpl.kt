package net.kigawa.keruta.ktcl.k8s.kicp

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.err.PeerVerificationErr
import net.kigawa.keruta.kicp.domain.identity.RegisterId
import net.kigawa.keruta.kicp.domain.repo.PeerServerClient
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.kodel.api.err.Res

/** ktse（idServerA）の verify-register エンドポイントに問い合わせる実装 */
class PeerServerClientImpl(
    private val httpClient: HttpClient,
    private val ktseBaseUrl: String,
) : PeerServerClient {
    override suspend fun verifyRegister(registerId: RegisterId, registerToken: RegisterToken): Res<Unit, KicpErr> = try {
        val response: HttpResponse = httpClient.post("$ktseBaseUrl/api/kicp/verify-register") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "registerId" to registerId.value,
                    "registerToken" to registerToken.value,
                ),
            )
        }

        if (response.status == HttpStatusCode.OK) {
            Res.Ok(Unit)
        } else {
            Res.Err(PeerVerificationErr("登録トークンの検証に失敗しました: ${response.status}"))
        }
    } catch (e: Exception) {
        Res.Err(PeerVerificationErr("ktseとの通信に失敗しました: ${e.message}", e))
    }
}
