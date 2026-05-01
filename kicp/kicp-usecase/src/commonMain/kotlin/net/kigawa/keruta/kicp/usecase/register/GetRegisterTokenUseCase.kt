package net.kigawa.keruta.kicp.usecase.register

import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.repo.CurrentTimeMs
import net.kigawa.keruta.kicp.domain.repo.RegisterTokenGenerator
import net.kigawa.keruta.kicp.domain.repo.RegisterTokenRecord
import net.kigawa.keruta.kicp.domain.repo.RegisterTokenRepository
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.kodel.api.err.Res

interface GetRegisterTokenUseCase {
    suspend fun getRegisterToken(input: GetRegisterTokenInput): Res<RegisterToken, KicpErr>
}

class GetRegisterTokenUseCaseImpl(
    private val tokenGenerator: RegisterTokenGenerator,
    private val tokenRepository: RegisterTokenRepository,
    private val currentTimeMs: CurrentTimeMs,
) : GetRegisterTokenUseCase {
    override suspend fun getRegisterToken(input: GetRegisterTokenInput): Res<RegisterToken, KicpErr> {
        val token = tokenGenerator.generate()
        val record = RegisterTokenRecord(
            token = token,
            creatorIdentityId = input.identityId,
            expiresAtEpochMs = currentTimeMs.now() + input.validForMs,
        )
        return when (val r = tokenRepository.save(record)) {
            is Res.Err -> r.convert()
            is Res.Ok -> Res.Ok(token)
        }
    }
}
