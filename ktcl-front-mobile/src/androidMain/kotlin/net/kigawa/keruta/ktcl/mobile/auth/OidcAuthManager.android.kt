package net.kigawa.keruta.ktcl.mobile.auth

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.suspendCancellableCoroutine
import net.kigawa.keruta.ktcl.mobile.config.MobileConfig
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class OidcAuthManager actual constructor(
    private val config: MobileConfig,
) {
    private lateinit var context: Context
    private lateinit var authService: AuthorizationService
    private var authState: AuthState? = null
    private var pendingAuthRequest: AuthorizationRequest? = null

    fun initialize(context: Context) {
        this.context = context
        this.authService = AuthorizationService(context)
    }

    suspend fun getAuthorizationIntent(): Result<Intent> = suspendCancellableCoroutine { continuation ->
        val issuerUri = "${config.keycloakUrl}realms/${config.keycloakRealm}"

        AuthorizationServiceConfiguration.fetchFromIssuer(
            android.net.Uri.parse(issuerUri),
        ) { serviceConfig, ex ->
            if (ex != null) {
                continuation.resume(Result.failure(ex))
                return@fetchFromIssuer
            }

            val authRequest = AuthorizationRequest.Builder(
                serviceConfig!!,
                config.keycloakClientId,
                ResponseTypeValues.CODE,
                android.net.Uri.parse("net.kigawa.keruta.mobile:/oauth2redirect"),
            ).setScope("openid profile email")
                .build()

            pendingAuthRequest = authRequest
            val authIntent = authService.getAuthorizationRequestIntent(authRequest)
            continuation.resume(Result.success(authIntent))
        }
    }

    suspend fun exchangeToken(response: AuthorizationResponse): Result<String> =
        suspendCancellableCoroutine { continuation ->
            val tokenRequest = response.createTokenExchangeRequest()

            authService.performTokenRequest(tokenRequest) { tokenResponse, ex ->
                if (ex != null) {
                    continuation.resume(Result.failure(ex))
                    return@performTokenRequest
                }

                if (tokenResponse == null) {
                    continuation.resume(Result.failure(Exception("トークンレスポンスがnullです")))
                    return@performTokenRequest
                }

                authState = AuthState(response, tokenResponse, ex)
                val accessToken = tokenResponse.accessToken
                if (accessToken != null) {
                    continuation.resume(Result.success(accessToken))
                } else {
                    continuation.resume(Result.failure(Exception("アクセストークンがnullです")))
                }
            }
        }

    fun handleAuthorizationResponse(intent: Intent): Result<AuthorizationResponse> {
        val response = AuthorizationResponse.fromIntent(intent)
        val ex = AuthorizationException.fromIntent(intent)

        if (ex != null) {
            return Result.failure(ex)
        }

        if (response == null) {
            return Result.failure(Exception("認証レスポンスがnullです"))
        }

        return Result.success(response)
    }

    actual suspend fun login(): Result<String> {
        return Result.failure(NotImplementedError("Androidではlogin()の代わりにgetAuthorizationIntent()を使用してください"))
    }

    actual suspend fun logout() {
        authState = null
        authService.dispose()
    }

    actual suspend fun refreshToken(): Result<String> = suspendCancellableCoroutine { continuation ->
        val currentAuthState = authState
        if (currentAuthState == null) {
            continuation.resume(Result.failure(Exception("認証されていません")))
            return@suspendCancellableCoroutine
        }

        currentAuthState.performActionWithFreshTokens(authService) { accessToken, _, ex ->
            if (ex != null) {
                continuation.resume(Result.failure(ex))
                return@performActionWithFreshTokens
            }

            if (accessToken != null) {
                continuation.resume(Result.success(accessToken))
            } else {
                continuation.resume(Result.failure(Exception("アクセストークンがnullです")))
            }
        }
    }

    actual suspend fun exchangeCodeForToken(code: String): Result<String> {
        return Result.failure(NotImplementedError("AndroidではexchangeToken(response)を使用してください"))
    }

    actual fun isAuthenticated(): Boolean {
        return authState?.isAuthorized == true
    }

    actual fun getAccessToken(): String? {
        return authState?.accessToken
    }
}
