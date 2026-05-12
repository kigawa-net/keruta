package net.kigawa.keruta.kise.kicp

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import net.kigawa.keruta.kicp.domain.repo.RegisterTokenRepository
import net.kigawa.keruta.kicp.domain.token.RegisterToken
import net.kigawa.keruta.kicp.usecase.login.LoginUseCase
import net.kigawa.keruta.kicp.usecase.login.LoginUseCaseImpl
import net.kigawa.keruta.kicp.usecase.register.GetRegisterTokenUseCase
import net.kigawa.keruta.kicp.usecase.register.GetRegisterTokenUseCaseImpl
import net.kigawa.keruta.kicp.usecase.register.RegisterUseCase
import net.kigawa.keruta.kicp.usecase.register.RegisterUseCaseImpl
import net.kigawa.keruta.kicp.usecase.register.VerifyRegisterTokenUseCase
import net.kigawa.keruta.kicp.usecase.register.VerifyRegisterTokenUseCaseImpl
import java.util.UUID

object KicpFactory {
    fun createHttpClient(): HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
    }

    fun createLoginUseCase(httpClient: HttpClient): LoginUseCase = LoginUseCaseImpl(
        jwksRepository = JwksRepositoryImpl(httpClient),
        jwtVerifier = JwtVerifierImpl(),
    )

    fun createGetRegisterTokenUseCase(tokenRepository: RegisterTokenRepository): GetRegisterTokenUseCase = GetRegisterTokenUseCaseImpl(
        tokenGenerator = { RegisterToken(UUID.randomUUID().toString()) },
        tokenRepository = tokenRepository,
        currentTimeMs = { System.currentTimeMillis() },
    )

    fun createRegisterUseCase(httpClient: HttpClient, peerServerBaseUrl: String): RegisterUseCase = RegisterUseCaseImpl(
        jwksRepository = JwksRepositoryImpl(httpClient),
        jwtVerifier = JwtVerifierImpl(),
        peerServerClient = PeerServerClientImpl(httpClient, peerServerBaseUrl),
    )

    fun createVerifyRegisterTokenUseCase(tokenRepository: RegisterTokenRepository): VerifyRegisterTokenUseCase = VerifyRegisterTokenUseCaseImpl(tokenRepository = tokenRepository)
}
