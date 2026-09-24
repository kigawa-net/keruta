package net.kigawa.keruta.kicp.usecase.register

import net.kigawa.keruta.kicp.domain.err.KicpErr
import net.kigawa.keruta.kicp.domain.err.RegisterTokenExpiredErr
import net.kigawa.keruta.kicp.domain.err.RegisterTokenNotFoundErr
import net.kigawa.keruta.kicp.domain.identity.IdentityId
import net.kigawa.keruta.kicp.domain.repo.RegisterTokenRepository
import net.kigawa.kodel.api.err.Res

interface VerifyRegisterTokenUseCase {
    suspend fun verify(input: VerifyRegisterInput): Res<IdentityId, KicpErr>
}

/** idServerA 側で動作する登録トークン検証ユースケース */
class VerifyRegisterTokenUseCaseImpl(
    private val tokenRepository: RegisterTokenRepository,
) : VerifyRegisterTokenUseCase {
    override suspend fun verify(input: VerifyRegisterInput): Res<IdentityId, KicpErr> {
        val record = when (val r = tokenRepository.find(input.registerToken)) {
            is Res.Err -> return r.convert()
            is Res.Ok -> r.value ?: return Res.Err(RegisterTokenNotFoundErr())
        }

        if (record.expiresAtEpochMs < input.currentTimeMs) {
            tokenRepository.delete(input.registerToken)
            return Res.Err(RegisterTokenExpiredErr())
        }

        when (val r = tokenRepository.delete(input.registerToken)) {
            is Res.Err -> return r.convert()
            is Res.Ok -> Unit
        }

        return Res.Ok(record.creatorIdentityId)
    }
}
