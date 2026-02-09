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
        val responseResult = container.oidcAuthManager.handleAuthorizationResponse(intent)

        responseResult.onSuccess { response ->
            lifecycleScope.launch {
                val tokenResult = container.oidcAuthManager.exchangeToken(response)
                tokenResult.onSuccess { accessToken ->
                    container.secureStorage.saveUserToken(accessToken)
                    val tokens = TokenPair(
                        userToken = accessToken,
                        serverToken = "",
                    )
                    container.authService.setAuthenticated(tokens)
                    container.navigationController.navigateToRoot(Screen.QueueList)
                }.onFailure { error ->
                    container.authService.setError(error.message ?: "トークン交換エラー")
                }
            }
        }.onFailure { error ->
            container.authService.setError(error.message ?: "認証レスポンスエラー")
        }
    }
}
