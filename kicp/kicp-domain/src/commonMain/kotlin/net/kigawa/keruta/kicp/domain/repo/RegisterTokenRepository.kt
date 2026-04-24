package net.kigawa.keruta.kicp.domain.repo

import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.kodel.api.err.Res

interface RegisterTokenRepository {
    suspend fun save(record: RegisterTokenRecord): Res<Unit, KicpErr>
    suspend fun find(token: RegisterToken): Res<RegisterTokenRecord?, KicpErr>
    suspend fun delete(token: RegisterToken): Res<Unit, KicpErr>
}
