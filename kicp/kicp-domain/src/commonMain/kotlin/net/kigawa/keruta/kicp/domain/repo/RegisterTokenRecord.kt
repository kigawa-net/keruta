package net.kigawa.keruta.kicp.domain.repo

import net.kigawa.keruta.kicp.domain.identity.IdentityId
import net.kigawa.keruta.kicp.domain.token.RegisterToken

data class RegisterTokenRecord(
    val token: RegisterToken,
    val creatorIdentityId: IdentityId,
    val expiresAtEpochMs: Long,
)
