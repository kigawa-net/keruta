package net.kigawa.keruta.ktcl.k8s.kicp

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import net.kigawa.keruta.kicp.usecase.login.LoginUseCase
import net.kigawa.keruta.kicp.usecase.login.LoginUseCaseImpl
import net.kigawa.keruta.kicp.usecase.register.RegisterUseCase
import net.kigawa.keruta.kicp.usecase.register.RegisterUseCaseImpl

object KicpFactory {
    fun createHttpClient(): HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    fun createLoginUseCase(httpClient: HttpClient): LoginUseCase = LoginUseCaseImpl(
        jwksRepository = JwksRepositoryImpl(httpClient),
        jwtVerifier = JwtVerifierImpl(),
    )

    fun createRegisterUseCase(httpClient: HttpClient, ktseBaseUrl: String): RegisterUseCase = RegisterUseCaseImpl(
        jwksRepository = JwksRepositoryImpl(httpClient),
        jwtVerifier = JwtVerifierImpl(),
        peerServerClient = PeerServerClientImpl(httpClient, ktseBaseUrl),
    )
}
