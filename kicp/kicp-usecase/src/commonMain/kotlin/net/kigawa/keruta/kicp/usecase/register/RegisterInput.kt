package net.kigawa.keruta.kicp.usecase.register

import net.kigawa.keruta.kicp.domain.jwks.JwksUrl
import net.kigawa.keruta.kicp.domain.token.OidcToken
import net.kigawa.keruta.kicp.domain.token.ProviderToken
import net.kigawa.keruta.kicp.domain.token.RegisterToken

data class RegisterInput(
    val oidcToken: OidcToken,
    val oidcJwksUrl: JwksUrl,
    val providerToken: ProviderToken,
    val providerJwksUrl: JwksUrl,
    val registerToken: RegisterToken,
)
