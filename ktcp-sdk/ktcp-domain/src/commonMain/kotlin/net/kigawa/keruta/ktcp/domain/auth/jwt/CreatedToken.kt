package net.kigawa.keruta.ktcp.domain.auth.jwt

import net.kigawa.keruta.ktcp.domain.auth.key.PemKey

data class CreatedToken(
    val rawToken: RawJwtToken,
    val issuer: Issuer,
    val subject: Subject,
    val audience: Audience,
    val pemKey: PemKey,
) {
    override fun toString(): String {
        return "CreatedToken(rawToken=$rawToken, issuer=$issuer, subject=$subject, audience=$audience, pemKey=***)"
    }
}
