package net.kigawa.keruta.ktcp.usecase.client

import net.kigawa.keruta.ktcp.domain.KtclAudience
import net.kigawa.keruta.ktcp.domain.KtclIssuer
import net.kigawa.keruta.ktcp.domain.UserSubject
import net.kigawa.keruta.ktcp.domain.auth.key.PemKey
import net.kigawa.keruta.ktcp.domain.client.CreatedKtclToken
import net.kigawa.keruta.ktcp.usecase.JwtTokenCreator

class ProviderTokenCreator(
    private val privateKey: PemKey,
    private val ktclIssuer: KtclIssuer,
    private val ktclAudience: KtclAudience,
    private val tokenCreator: JwtTokenCreator,
) {
    fun create(userSubject: UserSubject): CreatedKtclToken =
        CreatedKtclToken(tokenCreator.create(privateKey, ktclIssuer, ktclAudience, userSubject))

}
