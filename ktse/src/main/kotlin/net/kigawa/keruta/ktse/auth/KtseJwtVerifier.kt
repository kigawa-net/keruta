package net.kigawa.keruta.ktse.auth

import com.auth0.jwt.JWT
import net.kigawa.keruta.ktcp.base.auth.VerifyFailErr
import net.kigawa.keruta.ktcp.base.auth.jwt.Auth0JwtVerifier
import net.kigawa.keruta.ktcp.base.auth.key.Auth0AlgorithmInitializer
import net.kigawa.keruta.ktcp.base.auth.key.JavaKeyPairInitializer
import net.kigawa.keruta.ktcp.model.auth.AuthToken
import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifier
import net.kigawa.keruta.ktcp.model.auth.jwt.JwtVerifyValues
import net.kigawa.keruta.ktcp.model.auth.jwt.UnverifiedToken
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifyErr
import net.kigawa.keruta.ktcp.model.auth.key.PemKey
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.kodel.api.err.Res
import java.util.*

class KtseJwtVerifier(
    private val auth0JwtVerifier: Auth0JwtVerifier,
    private val pemKey: PemKey,
    val auth0AlgorithmInitializer: Auth0AlgorithmInitializer,
    val javaKeyPairInitializer: JavaKeyPairInitializer,
): JwtVerifier {

    override fun decodeUnverified(userToken: AuthToken): Res<UnverifiedToken, VerifyErr> =
        auth0JwtVerifier.decodeUnverified(userToken)

    override fun createToken(jwtVerifyValues: JwtVerifyValues): Res<AuthToken, KtcpErr> = try {
        val algorithm = auth0AlgorithmInitializer.initPrivateKey(
            javaKeyPairInitializer.initialize(pemKey)
        )
        val token = JWT.create()
            .withIssuer(jwtVerifyValues.issuer.toStrUrl())
            .withAudience(jwtVerifyValues.audience)
            .withSubject(jwtVerifyValues.subject)
            .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
            .sign(algorithm)
        Res.Ok(token)
    } catch (e: Exception) {
        Res.Err(VerifyFailErr("JWT creation failed: ${e.message}", e))
    }
}
