package net.kigawa.keruta.ktse.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

class VerifierProvider {

    fun verifier(
        alg: Algorithm, issuer: String, audience: String,
        subject: String,
    ) = JWT.require(alg)
        .withIssuer(issuer)
        .withAudience(audience)
        .withSubject(subject)
        .build()
}
