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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class OidcAuthManager actual constructor(
    private val config: MobileConfig,
) {
    private lateinit var context: Context
    private lateinit var authService: AuthorizationService
    private var authState: AuthState? = null

    fun initialize(context: Context) {
        this.context = context
        this.authService = AuthorizationService(context)
    }

    actual suspend fun login(): Result<String> = suspendCancellableCoroutine { continuation ->
        val issuerUri = "${config.keycloakUrl}realms/${config.keycloakRealm}"

        AuthorizationServiceConfiguration.fetchFromIssuer(
            android.net.Uri.parse(issuerUri),
        ) { serviceConfig, ex ->
            if (ex != null) {
                continuation.resumeWithException(ex)
                return@fetchFromIssuer
            }

            val authRequest = AuthorizationRequest.Builder(
                serviceConfig!!,
                config.keycloakClientId,
                ResponseTypeValues.CODE,
                android.net.Uri.parse("net.kigawa.keruta.mobile:/oauth2redirect"),
            ).setScope("openid profile email")
                .build()

            val authIntent = authService.getAuthorizationRequestIntent(authRequest)

            continuation.resume(Result.success(""))
        }
    }

    actual suspend fun logout() {
        authState = null
        authService.dispose()
    }

    actual suspend fun refreshToken(): Result<String> {
        return Result.failure(NotImplementedError("リフレッシュトークン未実装"))
    }

    actual fun isAuthenticated(): Boolean {
        return authState?.isAuthorized == true
    }

    fun handleAuthorizationResponse(intent: Intent): Result<String> {
        val response = AuthorizationResponse.fromIntent(intent)
        val ex = AuthorizationException.fromIntent(intent)

        authState = AuthState(response, ex)

        if (response != null) {
            return Result.success(response.authorizationCode ?: "")
        }

        return Result.failure(ex ?: Exception("認証に失敗しました"))
    }
}
