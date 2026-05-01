package net.kigawa.keruta.kicp.usecase.register

import net.kigawa.keruta.kicp.domain.identity.IdentityId

data class GetRegisterTokenInput(
    val identityId: IdentityId,
    val validForMs: Long = 5 * 60 * 1000L,
)
