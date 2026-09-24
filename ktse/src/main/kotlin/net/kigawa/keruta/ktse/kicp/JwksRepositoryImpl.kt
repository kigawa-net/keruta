package net.kigawa.keruta.ktse.kicp

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import net.kigawa.keruta.kicp.domain.err.JwksFetchErr
import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.jwks.Jwks
import net.kigawa.keruta.kicp.domain.jwks.JwksUrl
import net.kigawa.keruta.kicp.domain.repo.JwksRepository
import net.kigawa.kodel.api.err.Res

/**
 * JWKSをHTTPで取得する実装
 */
class JwksRepositoryImpl(
    private val httpClient: HttpClient,
) : JwksRepository {
    override suspend fun get(url: JwksUrl): Res<Jwks, KicpErr> {
        return try {
            val jwks: Jwks = httpClient.get(url.value).body()
            Res.Ok(jwks)
        } catch (e: Exception) {
            Res.Err(JwksFetchErr(url.value, e))
        }
    }
}
