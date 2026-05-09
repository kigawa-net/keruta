package net.kigawa.keruta.ktse.kicp

import net.kigawa.keruta.kicp.domain.repo.RegisterTokenRepository
import net.kigawa.keruta.kicp.usecase.register.VerifyRegisterTokenUseCase
import net.kigawa.keruta.kicp.usecase.register.VerifyRegisterTokenUseCaseImpl

/**
 * VerifyRegisterTokenUseCaseのFactoryクラス
 */
object VerifyRegisterTokenUseCaseFactory {
    fun create(tokenRepository: RegisterTokenRepository): VerifyRegisterTokenUseCase {
        return VerifyRegisterTokenUseCaseImpl(
            tokenRepository = tokenRepository,
        )
    }
}
