package net.kigawa.keruta.ktcp.usecase

import net.kigawa.keruta.ktcp.domain.auth.jwt.Audience
import net.kigawa.keruta.ktcp.domain.auth.jwt.CreatedToken
import net.kigawa.keruta.ktcp.domain.auth.jwt.Issuer
import net.kigawa.keruta.ktcp.domain.auth.jwt.Subject
import net.kigawa.keruta.ktcp.domain.auth.key.PemKey

interface JwtTokenCreator {
    fun create(pemKey: PemKey, issuer: Issuer, audience: Audience, subject: Subject): CreatedToken
}
