package net.kigawa.keruta.ktse.kicp

import net.kigawa.keruta.kicp.domain.repo.RegisterTokenRepository
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.keruta.kicp.usecase.register.GetRegisterTokenUseCase
import net.kigawa.keruta.kicp.usecase.register.GetRegisterTokenUseCaseImpl
import java.util.UUID

/**
 * GetRegisterTokenUseCaseのFactoryクラス
 */
object GetRegisterTokenUseCaseFactory {
    fun create(tokenRepository: RegisterTokenRepository): GetRegisterTokenUseCase {
        return GetRegisterTokenUseCaseImpl(
            tokenGenerator = { RegisterToken(UUID.randomUUID().toString()) },
            tokenRepository = tokenRepository,
            currentTimeMs = { System.currentTimeMillis() },
        )
    }
}
