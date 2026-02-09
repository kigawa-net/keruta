package net.kigawa.keruta.ktcl.mobile

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.auth.TokenPair
import net.kigawa.keruta.ktcl.mobile.di.IosAppContainer
import net.kigawa.keruta.ktcl.mobile.navigation.Screen
import net.kigawa.keruta.ktcl.mobile.ui.App

fun MainViewController() = ComposeUIViewController {
    val container = IosAppContainer()
    container.initialize()

    val scope = CoroutineScope(Dispatchers.Main)

    App(
        container = container,
        onLoginRequest = {
            scope.launch {
                val codeResult = container.oidcAuthManager.login()
                codeResult.onSuccess { code ->
                    val tokenResult = container.oidcAuthManager.exchangeCodeForToken(code)
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
                }.onFailure { error ->
                    container.authService.setError(error.message ?: "認証エラー")
                }
            }
        },
    )
}
