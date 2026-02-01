package net.kigawa.keruta.ktse.auth.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm

class VerifierProvider {

    fun verifier(
        alg: Algorithm, issuer: String, audience: String,
        subject: String,
    ): JWTVerifier = JWT.require(alg)
        .withIssuer(issuer)
        .withAudience(audience)
        .withSubject(subject)
        .build()
}
