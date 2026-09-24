package net.kigawa.keruta.kise.kicp

import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.repo.RegisterTokenRecord
import net.kigawa.keruta.kicp.domain.repo.RegisterTokenRepository
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.kodel.api.err.Res

class InMemoryRegisterTokenRepository : RegisterTokenRepository {
    private val storage = mutableMapOf<RegisterToken, RegisterTokenRecord>()

    override suspend fun save(record: RegisterTokenRecord): Res<Unit, KicpErr> {
        storage[record.token] = record
        return Res.Ok(Unit)
    }

    override suspend fun find(token: RegisterToken): Res<RegisterTokenRecord?, KicpErr> = Res.Ok(storage[token])

    override suspend fun delete(token: RegisterToken): Res<Unit, KicpErr> {
        storage.remove(token)
        return Res.Ok(Unit)
    }
}
