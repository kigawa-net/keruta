package net.kigawa.keruta.kicp.usecase.login

import net.kigawa.keruta.kicp.domain.jwks.JwksUrl
import net.kigawa.keruta.kicp.domain.token.OidcToken
import net.kigawa.keruta.kicp.domain.token.ProviderToken

data class LoginInput(
    val oidcToken: OidcToken,
    val oidcJwksUrl: JwksUrl,
    val providerToken: ProviderToken,
    val providerJwksUrl: JwksUrl,
)
