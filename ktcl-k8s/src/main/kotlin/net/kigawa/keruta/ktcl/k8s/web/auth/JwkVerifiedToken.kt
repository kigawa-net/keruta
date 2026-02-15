package net.kigawa.keruta.ktcl.k8s.web.auth

import com.auth0.jwt.interfaces.DecodedJWT
import net.kigawa.keruta.ktcp.model.auth.jwt.VerifiedToken
import net.kigawa.kodel.api.net.Url

class JwkVerifiedToken(decodedJwt: DecodedJWT) : VerifiedToken {
    override val audience: String = decodedJwt.audience.firstOrNull() ?: ""
    override val issuer: Url = Url.parse(decodedJwt.issuer)
    override val subject: String = decodedJwt.subject
}
