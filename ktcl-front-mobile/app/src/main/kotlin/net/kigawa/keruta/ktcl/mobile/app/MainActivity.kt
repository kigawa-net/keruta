package net.kigawa.keruta.ktcl.mobile.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.auth.TokenPair
import net.kigawa.keruta.ktcl.mobile.di.AndroidAppContainer
import net.kigawa.keruta.ktcl.mobile.navigation.Screen
import net.kigawa.keruta.ktcl.mobile.ui.App

class MainActivity : ComponentActivity() {
    private lateinit var container: AndroidAppContainer

    private val authLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        result.data?.let { intent ->
            handleAuthorizationResponse(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        container = AndroidAppContainer(this)
        container.initialize()

        setContent {
            App(
                container = container,
                onLoginRequest = { startOidcLogin() },
            )
        }
    }

    private fun startOidcLogin() {
        lifecycleScope.launch {
            val intentResult = container.oidcAuthManager.getAuthorizationIntent()
            intentResult.onSuccess { intent ->
                authLauncher.launch(intent)
            }.onFailure { error ->
                container.authService.setError(error.message ?: "認証エラー")
            }
        }
    }

    private fun handleAuthorizationResponse(intent: android.content.Intent) {
        println("=== handleAuthorizationResponse called ===")
        val responseResult = container.oidcAuthManager.handleAuthorizationResponse(intent)

        responseResult.onSuccess { response ->
            println("=== handleAuthorizationResponse: response success ===")
            lifecycleScope.launch {
                val tokenResult = container.oidcAuthManager.exchangeToken(response)
                tokenResult.onSuccess { accessToken ->
                    println("=== handleAuthorizationResponse: got accessToken ===")
                    container.secureStorage.saveUserToken(accessToken)

                    // サーバーからserverTokenを取得
                    val serverTokenResult = container.tokenManager.getServerToken(accessToken)
                    serverTokenResult.onSuccess { serverToken ->
                        println("=== handleAuthorizationResponse: got serverToken ===")
                        val tokens = TokenPair(
                            userToken = accessToken,
                            serverToken = serverToken,
                        )
                        container.authService.setAuthenticated(tokens)

                        // WebSocketに接続
                        container.connectWebSocket(
                            onConnected = {
                                println("=== handleAuthorizationResponse: WebSocket connected ===")
                                container.navigationController.navigateToRoot(Screen.QueueList)
                            },
                            onError = { error ->
                                println("=== handleAuthorizationResponse: WebSocket error: ${error.message} ===")
                                container.authService.setError("WebSocket接続に失敗しました: ${error.message}")
                            }
                        )
                    }.onFailure { error ->
                        println("=== handleAuthorizationResponse: serverToken error: ${error.message} ===")
                        container.authService.setError("サーバーtoken取得エラー: ${error.message}")
                    }
                }.onFailure { error ->
                    println("=== handleAuthorizationResponse: exchangeToken error: ${error.message} ===")
                    container.authService.setError(error.message ?: "トークン交換エラー")
                }
            }
        }.onFailure { error ->
            println("=== handleAuthorizationResponse: response error: ${error.message} ===")
            container.authService.setError(error.message ?: "認証レスポンスエラー")
        }
    }
}
