package net.kigawa.keruta.kicp.domain.repo

import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.identity.RegisterId
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.kodel.api.err.Res

interface PeerServerClient {
    suspend fun verifyRegister(registerId: RegisterId, registerToken: RegisterToken): Res<Unit, KicpErr>
}
