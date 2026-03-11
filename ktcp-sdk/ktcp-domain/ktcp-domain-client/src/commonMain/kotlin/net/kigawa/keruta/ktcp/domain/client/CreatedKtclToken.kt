package net.kigawa.keruta.ktcp.domain.client

import net.kigawa.keruta.ktcp.domain.auth.jwt.CreatedToken

data class CreatedKtclToken(
    val createdToken: CreatedToken,
)
