package net.kigawa.keruta.ktcl.k8s.kicp

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import net.kigawa.keruta.kicp.domain.claims.TokenClaims
import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.err.TokenVerificationErr
import net.kigawa.keruta.kicp.domain.jwks.JwkKey
import net.kigawa.keruta.kicp.domain.jwks.Jwks
import net.kigawa.keruta.kicp.domain.repo.JwtVerifier
import net.kigawa.kodel.api.err.Res
import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.Base64

class JwtVerifierImpl : JwtVerifier {
    override suspend fun verify(rawToken: String, jwks: Jwks): Res<TokenClaims, KicpErr> = try {
        val decodedJwt = JWT.decode(rawToken)
        val kid = decodedJwt.keyId

        val jwkKey = jwks.keys.find { it.kid == kid }
            ?: return Res.Err(TokenVerificationErr("適切なJWKキーが見つかりません: kid=$kid"))

        val publicKey = buildRsaPublicKey(jwkKey)

        val algorithm = when (jwkKey.alg) {
            "RS256" -> Algorithm.RSA256(publicKey, null)
            "RS384" -> Algorithm.RSA384(publicKey, null)
            "RS512" -> Algorithm.RSA512(publicKey, null)
            else -> return Res.Err(TokenVerificationErr("サポートされていないアルゴリズム: ${jwkKey.alg}"))
        }

        val verifiedJwt = JWT.require(algorithm).build().verify(rawToken)

        Res.Ok(
            TokenClaims(
                issuer = verifiedJwt.issuer ?: "",
                subject = verifiedJwt.subject ?: "",
                audience = verifiedJwt.audience ?: emptyList(),
            ),
        )
    } catch (e: Exception) {
        Res.Err(TokenVerificationErr("JWTの検証に失敗しました: ${e.message}", e))
    }

    private fun buildRsaPublicKey(jwkKey: JwkKey): RSAPublicKey {
        val n = BigInteger(1, Base64.getUrlDecoder().decode(jwkKey.n))
        val e = BigInteger(1, Base64.getUrlDecoder().decode(jwkKey.e))
        return KeyFactory.getInstance("RSA").generatePublic(RSAPublicKeySpec(n, e)) as RSAPublicKey
    }
}
