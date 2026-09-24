package net.kigawa.keruta.kicp.usecase.register

import net.kigawa.keruta.kicp.domain.identity.RegisterId
import net.kigawa.keruta.kicp.domain.token.RegisterToken

data class VerifyRegisterInput(
    val registerId: RegisterId,
    val registerToken: RegisterToken,
    val currentTimeMs: Long,
)
