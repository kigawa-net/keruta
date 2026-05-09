package net.kigawa.keruta.ktse.kicp

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import net.kigawa.keruta.kicp.usecase.login.LoginUseCase
import net.kigawa.keruta.kicp.usecase.login.LoginUseCaseImpl

/**
 * LoginUseCaseのFactoryクラス
 */
object LoginUseCaseFactory {
    fun create(): LoginUseCase {
        val httpClient = HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
        return LoginUseCaseImpl(
            jwksRepository = JwksRepositoryImpl(httpClient),
            jwtVerifier = JwtVerifierImpl(),
        )
    }
}
