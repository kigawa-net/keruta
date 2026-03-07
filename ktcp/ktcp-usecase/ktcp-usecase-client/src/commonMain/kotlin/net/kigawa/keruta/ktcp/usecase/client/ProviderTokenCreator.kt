package net.kigawa.keruta.ktcp.usecase.client

import net.kigawa.keruta.ktcp.model.auth.key.PemKey
import net.kigawa.keruta.ktcp.model.client.CreatedProviderToken
import net.kigawa.keruta.ktcp.model.client.KtclIssuer
import net.kigawa.keruta.ktcp.model.client.ProviderAudience
import net.kigawa.keruta.ktcp.model.client.UserSubject
import net.kigawa.keruta.ktcp.usecase.JwtTokenCreator

class ProviderTokenCreator(
    private val privateKey: PemKey,
    private val ktclIssuer: KtclIssuer,
    private val providerAudience: ProviderAudience,
    private val tokenCreator: JwtTokenCreator,
) {
    fun create(userSubject: UserSubject): CreatedProviderToken =
        CreatedProviderToken(tokenCreator.create(privateKey, ktclIssuer, providerAudience, userSubject))

}
