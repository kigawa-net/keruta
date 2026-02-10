package net.kigawa.keruta.ktcl.mobile.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.kigawa.keruta.ktcl.mobile.auth.OidcAuthManager
import net.kigawa.keruta.ktcl.mobile.auth.TokenManager
import net.kigawa.keruta.ktcl.mobile.config.MobileConfig
import net.kigawa.keruta.ktcl.mobile.connection.ConnectionManager
import net.kigawa.keruta.ktcl.mobile.navigation.NavigationController
import net.kigawa.keruta.ktcl.mobile.navigation.Screen
import net.kigawa.keruta.ktcl.mobile.provider.ProviderRepository
import net.kigawa.keruta.ktcl.mobile.queue.QueueRepository
import net.kigawa.keruta.ktcl.mobile.service.AuthService
import net.kigawa.keruta.ktcl.mobile.service.MessageSender
import net.kigawa.keruta.ktcl.mobile.storage.SecureStorage
import net.kigawa.keruta.ktcl.mobile.task.TaskRepository
import net.kigawa.keruta.ktcl.mobile.viewmodel.AuthViewModel
import net.kigawa.keruta.ktcl.mobile.viewmodel.ProviderCreateViewModel
import net.kigawa.keruta.ktcl.mobile.viewmodel.ProviderListViewModel
import net.kigawa.keruta.ktcl.mobile.viewmodel.QueueCreateViewModel
import net.kigawa.keruta.ktcl.mobile.viewmodel.QueueDetailViewModel
import net.kigawa.keruta.ktcl.mobile.viewmodel.QueueListViewModel

open class AppContainer(
    val config: MobileConfig = MobileConfig.default(),
) {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val httpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }

    val secureStorage by lazy { SecureStorage() }

    val oidcAuthManager by lazy { OidcAuthManager(config) }

    val tokenManager by lazy {
        TokenManager(oidcAuthManager, secureStorage, config, httpClient)
    }

    val connectionManager by lazy { ConnectionManager(config) }

    val messageSender by lazy { MessageSender() }

    val authService by lazy { AuthService(messageSender) }

    val queueRepository by lazy { QueueRepository() }

    val taskRepository by lazy { TaskRepository() }

    val providerRepository by lazy { ProviderRepository() }

    val navigationController by lazy { NavigationController(Screen.Login) }

    fun createAuthViewModel(): AuthViewModel {
        return AuthViewModel(authService)
    }

    fun createQueueListViewModel(): QueueListViewModel {
        return QueueListViewModel(queueRepository, messageSender)
    }

    fun createQueueCreateViewModel(): QueueCreateViewModel {
        return QueueCreateViewModel(providerRepository, messageSender)
    }

    fun createQueueDetailViewModel(): QueueDetailViewModel {
        return QueueDetailViewModel(taskRepository, messageSender)
    }

    fun createProviderListViewModel(): ProviderListViewModel {
        return ProviderListViewModel(providerRepository, messageSender)
    }

    fun createProviderCreateViewModel(): ProviderCreateViewModel {
        return ProviderCreateViewModel(messageSender)
    }
}
