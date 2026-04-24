package net.kigawa.keruta.kicp.domain.repo

import net.kigawa.keruta.kicp.domain.claims.TokenClaims
import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.jwks.Jwks
import net.kigawa.kodel.api.err.Res

interface JwtVerifier {
    suspend fun verify(rawToken: String, jwks: Jwks): Res<TokenClaims, KicpErr>
}
