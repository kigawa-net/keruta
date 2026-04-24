package net.kigawa.keruta.kicp.domain.repo

import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.jwks.Jwks
import net.kigawa.keruta.kicp.domain.jwks.JwksUrl
import net.kigawa.kodel.api.err.Res

interface JwksRepository {
    suspend fun get(url: JwksUrl): Res<Jwks, KicpErr>
}
