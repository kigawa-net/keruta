package net.kigawa.keruta.ktcl.mobile.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import net.kigawa.keruta.ktcl.mobile.auth.AuthState
import net.kigawa.keruta.ktcl.mobile.di.AppContainer
import net.kigawa.keruta.ktcl.mobile.navigation.NavigationHost
import net.kigawa.keruta.ktcl.mobile.navigation.Screen
import net.kigawa.keruta.ktcl.mobile.ui.layout.MainLayout
import net.kigawa.keruta.ktcl.mobile.ui.screen.LoginScreen
import net.kigawa.keruta.ktcl.mobile.ui.screen.ProviderListScreen
import net.kigawa.keruta.ktcl.mobile.ui.screen.QueueCreateScreen
import net.kigawa.keruta.ktcl.mobile.ui.screen.QueueDetailScreen
import net.kigawa.keruta.ktcl.mobile.ui.screen.QueueListScreen

@Composable
fun App(
    container: AppContainer,
    onLoginRequest: () -> Unit = {},
) {
    val authViewModel = remember { container.createAuthViewModel() }
    val authState by authViewModel.state.collectAsState()

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            when (authState.authState) {
                is AuthState.Unauthenticated,
                is AuthState.Authenticating,
                is AuthState.Error,
                -> {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginClick = {
                            authViewModel.startLogin()
                            onLoginRequest()
                        },
                    )
                }
                is AuthState.Authenticated -> {
                    AuthenticatedApp(
                        container = container,
                        onLogout = {
                            authViewModel.logout()
                            container.navigationController.navigateToRoot(Screen.Login)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthenticatedApp(
    container: AppContainer,
    onLogout: () -> Unit,
) {
    val currentScreen by container.navigationController.currentScreen.collectAsState()

    MainLayout(
        currentScreen = currentScreen,
        onNavigate = { screen ->
            container.navigationController.navigateToRoot(screen)
        },
        onLogout = onLogout,
    ) {
        NavigationHost(controller = container.navigationController) { screen ->
            when (screen) {
                is Screen.Login -> {
                    container.navigationController.navigateToRoot(Screen.QueueList)
                }
                is Screen.QueueList -> {
                    val viewModel = remember { container.createQueueListViewModel() }
                    QueueListScreen(
                        viewModel = viewModel,
                        onQueueClick = { queueId ->
                            container.navigationController.navigate(Screen.QueueDetail(queueId))
                        },
                        onCreateQueueClick = {
                            container.navigationController.navigate(Screen.QueueCreate)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                is Screen.QueueCreate -> {
                    val viewModel = remember { container.createQueueCreateViewModel() }
                    QueueCreateScreen(
                        viewModel = viewModel,
                        onCreated = {
                            container.navigationController.navigateBack()
                        },
                        onCancel = {
                            container.navigationController.navigateBack()
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                is Screen.QueueDetail -> {
                    val viewModel = remember { container.createQueueDetailViewModel() }
                    QueueDetailScreen(
                        viewModel = viewModel,
                        queueId = screen.queueId,
                        onBack = {
                            container.navigationController.navigateBack()
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                is Screen.ProviderList -> {
                    val viewModel = remember { container.createProviderListViewModel() }
                    ProviderListScreen(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
