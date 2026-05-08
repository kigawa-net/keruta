package net.kigawa.keruta.ktse.kicp

import net.kigawa.keruta.kicp.usecase.register.VerifyRegisterTokenUseCase
import net.kigawa.keruta.kicp.usecase.register.VerifyRegisterTokenUseCaseImpl

/**
 * VerifyRegisterTokenUseCaseのFactoryクラス
 * インメモリリポジトリをシングルトンとして管理し、トークンの保存と検証で共有する
 */
object VerifyRegisterTokenUseCaseFactory {
    private val tokenRepository = InMemoryRegisterTokenRepository()
    
    /**
     * VerifyRegisterTokenUseCaseのインスタンスを作成する
     * 
     * @return VerifyRegisterTokenUseCaseのインスタンス
     */
    fun create(): VerifyRegisterTokenUseCase {
        return VerifyRegisterTokenUseCaseImpl(
            tokenRepository = tokenRepository,
        )
    }
    
    /**
     * トークンを保存するためのリポジトリを取得する（GetRegisterTokenUseCase用）
     */
    fun getTokenRepository(): InMemoryRegisterTokenRepository = tokenRepository
}
