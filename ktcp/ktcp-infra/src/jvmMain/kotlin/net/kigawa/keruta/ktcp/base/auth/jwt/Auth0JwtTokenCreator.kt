package net.kigawa.keruta.ktcp.base.auth.jwt

import com.auth0.jwt.JWT
import net.kigawa.keruta.ktcp.base.auth.key.Auth0AlgorithmInitializer
import net.kigawa.keruta.ktcp.base.auth.key.JavaKeyPairInitializer
import net.kigawa.keruta.ktcp.model.auth.jwt.Audience
import net.kigawa.keruta.ktcp.model.auth.jwt.CreatedToken
import net.kigawa.keruta.ktcp.model.auth.jwt.Issuer
import net.kigawa.keruta.ktcp.model.auth.jwt.Subject
import net.kigawa.keruta.ktcp.model.auth.key.PemKey
import net.kigawa.keruta.ktcp.usecase.JwtTokenCreator
import java.util.*

class Auth0JwtTokenCreator(
    private val javaKeyPairInitializer: JavaKeyPairInitializer,
): JwtTokenCreator {
    override fun create(
        pemKey: PemKey, issuer: Issuer,
        audience: Audience, subject: Subject,
    ): CreatedToken {
        val key = javaKeyPairInitializer.initialize(pemKey)
        val algorithm = Auth0AlgorithmInitializer().initPrivateKey(key)
        val rawJwtToken = JWT.create()
            .withIssuer(issuer.toStrUrl())
            .withAudience(audience)
            .withSubject(subject)
            .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
            .sign(algorithm)
        return CreatedToken(rawJwtToken, issuer, subject, audience, pemKey)
    }
}
