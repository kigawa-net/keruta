package net.kigawa.keruta.ktcl.mobile.auth

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.kigawa.keruta.ktcl.mobile.config.MobileConfig
import net.kigawa.keruta.ktcl.mobile.storage.SecureStorage

@Serializable
data class TokenPair(
    val userToken: String,
    val serverToken: String,
)

@Serializable
private data class TokenRequest(
    val token: String,
)

@Serializable
private data class TokenResponse(
    val token: String,
)

class TokenManager(
    private val oidcAuthManager: OidcAuthManager,
    private val secureStorage: SecureStorage,
    private val config: MobileConfig,
    private val httpClient: HttpClient,
) {
    suspend fun authenticate(): Result<TokenPair> {
        return try {
            val userToken = oidcAuthManager.login().getOrThrow()

            val serverToken = fetchServerToken(userToken).getOrThrow()

            secureStorage.saveUserToken(userToken)
            secureStorage.saveServerToken(serverToken)

            Result.success(TokenPair(userToken, serverToken))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStoredTokens(): Result<TokenPair?> {
        return try {
            val userToken = secureStorage.getUserToken()
            val serverToken = secureStorage.getServerToken()

            if (userToken != null && serverToken != null) {
                Result.success(TokenPair(userToken, serverToken))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearTokens() {
        secureStorage.clearTokens()
        oidcAuthManager.logout()
    }

    private suspend fun fetchServerToken(userToken: String): Result<String> {
        return try {
            val response = httpClient.post("${config.apiBaseUrl}api/token") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(TokenRequest.serializer(), TokenRequest(userToken)))
            }

            val tokenResponse = Json.decodeFromString<TokenResponse>(response.bodyAsText())
            Result.success(tokenResponse.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
