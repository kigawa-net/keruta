package net.kigawa.keruta.ktcp.model.client

import net.kigawa.keruta.ktcp.model.auth.jwt.CreatedToken

data class CreatedProviderToken(
    val createdToken: CreatedToken,
)
