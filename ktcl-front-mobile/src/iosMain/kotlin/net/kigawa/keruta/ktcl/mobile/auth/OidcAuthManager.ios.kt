package net.kigawa.keruta.ktcl.mobile.auth

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import net.kigawa.keruta.ktcl.mobile.config.MobileConfig
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.AuthenticationServices.ASWebAuthenticationSessionCompletionHandler
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class OidcAuthManager actual constructor(
    private val config: MobileConfig,
) {
    private var accessToken: String? = null
    private var authSession: ASWebAuthenticationSession? = null
    private val httpClient = HttpClient(Darwin)

    actual suspend fun login(): Result<String> = suspendCancellableCoroutine { continuation ->
        val issuerUri = "${config.keycloakUrl}realms/${config.keycloakRealm}"
        val authorizationEndpoint = "$issuerUri/protocol/openid-connect/auth"
        val redirectUri = "net.kigawa.keruta.mobile:/oauth2redirect"

        val urlComponents = NSURLComponents(authorizationEndpoint)
        urlComponents.queryItems = listOf(
            NSURLQueryItem("response_type", "code"),
            NSURLQueryItem("client_id", config.keycloakClientId),
            NSURLQueryItem("redirect_uri", redirectUri),
            NSURLQueryItem("scope", "openid profile email"),
        )

        val authUrl = urlComponents.URL
        if (authUrl == null) {
            continuation.resume(Result.failure(Exception("認証URLの構築に失敗しました")))
            return@suspendCancellableCoroutine
        }

        val completionHandler: ASWebAuthenticationSessionCompletionHandler = { callbackUrl: NSURL?, error: NSError? ->
            if (error != null) {
                continuation.resume(Result.failure(Exception(error.localizedDescription)))
            } else if (callbackUrl != null) {
                val code = extractCodeFromUrl(callbackUrl)
                if (code != null) {
                    continuation.resume(Result.success(code))
                } else {
                    continuation.resume(Result.failure(Exception("認証コードが見つかりません")))
                }
            } else {
                continuation.resume(Result.failure(Exception("認証がキャンセルされました")))
            }
        }

        authSession = ASWebAuthenticationSession(
            uRL = authUrl,
            callbackURLScheme = "net.kigawa.keruta.mobile",
            completionHandler = completionHandler,
        )

        authSession?.presentationContextProvider = PresentationContextProvider()
        authSession?.prefersEphemeralWebBrowserSession = true
        authSession?.start()

        continuation.invokeOnCancellation {
            authSession?.cancel()
        }
    }

    private fun extractCodeFromUrl(url: NSURL): String? {
        val urlComponents = NSURLComponents(url, false)
        val queryItems = urlComponents?.queryItems as? List<NSURLQueryItem> ?: return null
        return queryItems.find { it.name == "code" }?.value
    }

    actual suspend fun exchangeCodeForToken(code: String): Result<String> {
        val issuerUri = "${config.keycloakUrl}realms/${config.keycloakRealm}"
        val tokenEndpoint = "$issuerUri/protocol/openid-connect/token"
        val redirectUri = "net.kigawa.keruta.mobile:/oauth2redirect"

        return try {
            val response = httpClient.submitForm(
                url = tokenEndpoint,
                formParameters = parameters {
                    append("grant_type", "authorization_code")
                    append("code", code)
                    append("redirect_uri", redirectUri)
                    append("client_id", config.keycloakClientId)
                },
            )

            val responseText = response.bodyAsText()
            val accessTokenMatch = Regex("\"access_token\"\\s*:\\s*\"([^\"]+)\"")
                .find(responseText)
            val token = accessTokenMatch?.groupValues?.get(1)
                ?: return Result.failure(Exception("アクセストークンが見つかりません"))

            accessToken = token
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun logout() {
        accessToken = null
        authSession?.cancel()
        authSession = null
    }

    actual suspend fun refreshToken(): Result<String> {
        return Result.failure(NotImplementedError("リフレッシュトークン未実装"))
    }

    actual fun isAuthenticated(): Boolean {
        return accessToken != null
    }

    actual fun getAccessToken(): String? {
        return accessToken
    }
}

private class PresentationContextProvider :
    NSObject(),
    ASWebAuthenticationPresentationContextProvidingProtocol {

    override fun presentationAnchorForWebAuthenticationSession(
        session: ASWebAuthenticationSession,
    ): ASPresentationAnchor {
        return UIApplication.sharedApplication.keyWindow ?: UIWindow()
    }
}
