package net.kigawa.keruta.ktcl.k8s.kicp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import net.kigawa.keruta.kicp.domain.err.JwksFetchErr
import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.jwks.Jwks
import net.kigawa.keruta.kicp.domain.jwks.JwksUrl
import net.kigawa.keruta.kicp.domain.repo.JwksRepository
import net.kigawa.kodel.api.err.Res

class JwksRepositoryImpl(
    private val httpClient: HttpClient,
) : JwksRepository {
    override suspend fun get(url: JwksUrl): Res<Jwks, KicpErr> = try {
        val jwks: Jwks = httpClient.get(url.value).body()
        Res.Ok(jwks)
    } catch (e: Exception) {
        Res.Err(JwksFetchErr(url.value, e))
    }
}
