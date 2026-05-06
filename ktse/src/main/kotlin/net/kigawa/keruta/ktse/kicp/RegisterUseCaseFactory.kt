package net.kigawa.keruta.ktse.kicp

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import net.kigawa.keruta.kicp.usecase.register.RegisterUseCase
import net.kigawa.keruta.kicp.usecase.register.RegisterUseCaseImpl

/**
 * RegisterUseCaseのFactoryクラス（DIパターンに従って手動で依存性を注入）
 */
object RegisterUseCaseFactory {
    /**
     * RegisterUseCaseのインスタンスを作成する
     * 
     * @param peerServerBaseUrl 対端サーバー（idServerA）のベースURL
     * @return RegisterUseCaseのインスタンス
     */
    fun create(peerServerBaseUrl: String = "http://localhost:8080"): RegisterUseCase {
        val httpClient = HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
        
        val jwksRepository = JwksRepositoryImpl(httpClient)
        val jwtVerifier = JwtVerifierImpl()
        val peerServerClient = PeerServerClientImpl(httpClient, peerServerBaseUrl)
        
        return RegisterUseCaseImpl(
            jwksRepository = jwksRepository,
            jwtVerifier = jwtVerifier,
            peerServerClient = peerServerClient,
        )
    }
}
