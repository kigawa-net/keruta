package net.kigawa.keruta.ktse.kicp

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier as Auth0JWTVerifier
import net.kigawa.keruta.kicp.domain.claims.TokenClaims
import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.err.TokenVerificationErr
import net.kigawa.keruta.kicp.domain.jwks.Jwks
import net.kigawa.keruta.kicp.domain.jwks.JwkKey
import net.kigawa.keruta.kicp.domain.repo.JwtVerifier
import net.kigawa.kodel.api.err.Res
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.Base64

/**
 * Auth0のjava-jwtライブラリを使用してJWTを検証する実装
 */
class JwtVerifierImpl : JwtVerifier {
    override suspend fun verify(rawToken: String, jwks: Jwks): Res<TokenClaims, KicpErr> {
        return try {
            // JWKSから適切なキーを選択（kidでマッチング）
            val decodedJwt = JWT.decode(rawToken)
            val kid = decodedJwt.keyId
            
            val jwkKey = jwks.keys.find { it.kid == kid }
                ?: return Res.Err(TokenVerificationErr("適切なJWKキーが見つかりません: kid=$kid"))
            
            // RSA公開鍵を構築
            val publicKey = buildRsaPublicKey(jwkKey)
            
            // アルゴリズムを選択
            val algorithm = when (jwkKey.alg) {
                "RS256" -> Algorithm.RSA256(publicKey, null)
                "RS384" -> Algorithm.RSA384(publicKey, null)
                "RS512" -> Algorithm.RSA512(publicKey, null)
                else -> return Res.Err(TokenVerificationErr("サポートされていないアルゴリズムです: ${jwkKey.alg}"))
            }
            
            // JWTを検証
            val verifier: Auth0JWTVerifier = JWT.require(algorithm)
                .build()
            
            val verifiedJwt = verifier.verify(rawToken)
            
            // クレームを抽出
            val claims = TokenClaims(
                issuer = verifiedJwt.issuer ?: "",
                subject = verifiedJwt.subject ?: "",
                audience = verifiedJwt.audience ?: emptyList(),
            )
            
            Res.Ok(claims)
        } catch (e: Exception) {
            Res.Err(TokenVerificationErr("JWTの検証に失敗しました: ${e.message}", e))
        }
    }
    
    private fun buildRsaPublicKey(jwkKey: JwkKey): RSAPublicKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        
        val nBytes = Base64.getUrlDecoder().decode(jwkKey.n)
        val eBytes = Base64.getUrlDecoder().decode(jwkKey.e)
        
        val n = java.math.BigInteger(1, nBytes)
        val e = java.math.BigInteger(1, eBytes)
        
        val spec = RSAPublicKeySpec(n, e)
        return keyFactory.generatePublic(spec) as RSAPublicKey
    }
}
