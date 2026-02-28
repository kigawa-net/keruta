package net.kigawa.keruta.ktcl.mobile.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kigawa.keruta.ktcl.mobile.auth.AuthState
import net.kigawa.keruta.ktcl.mobile.auth.OidcAuthManager
import net.kigawa.keruta.ktcl.mobile.auth.TokenManager
import net.kigawa.keruta.ktcl.mobile.config.MobileConfig
import net.kigawa.keruta.ktcl.mobile.connection.ConnectionManager
import net.kigawa.keruta.ktcl.mobile.navigation.NavigationController
import net.kigawa.keruta.ktcl.mobile.navigation.Screen
import net.kigawa.keruta.ktcl.mobile.provider.ProviderRepository
import net.kigawa.keruta.ktcl.mobile.queue.QueueRepository
import net.kigawa.keruta.ktcl.mobile.service.AuthService
import platform.Foundation.NSLog
import net.kigawa.keruta.ktcl.mobile.service.MessageHandler
import net.kigawa.keruta.ktcl.mobile.service.MessageSender
import net.kigawa.keruta.ktcl.mobile.storage.SecureStorage
import net.kigawa.keruta.ktcl.mobile.task.TaskRepository
import net.kigawa.keruta.ktcl.mobile.viewmodel.AuthViewModel
import net.kigawa.keruta.ktcl.mobile.viewmodel.ProviderListViewModel
import net.kigawa.keruta.ktcl.mobile.viewmodel.QueueCreateViewModel
import net.kigawa.keruta.ktcl.mobile.viewmodel.QueueDetailViewModel
import net.kigawa.keruta.ktcl.mobile.viewmodel.QueueListViewModel
import net.kigawa.keruta.ktcl.mobile.viewmodel.TaskDetailViewModel

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

    private val messageHandlerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val messageHandler by lazy {
        MessageHandler(messageSender, queueRepository, taskRepository, providerRepository, messageHandlerScope)
    }

    val navigationController by lazy { NavigationController(Screen.Login) }

    // WebSocket接続 состояние
    private var _isWebSocketConnected = false

    /**
     * WebSocketに接続し、messageSenderに設定する
     * 接続後に自動て認証メッセージを送信する
     */
    fun connectWebSocket(onConnected: () -> Unit = {}, onError: (Throwable) -> Unit = {}) {
        NSLog("=== connectWebSocket called ===")
        if (_isWebSocketConnected) {
            NSLog("=== connectWebSocket: already connected ===")
            onConnected()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                NSLog("=== connectWebSocket: connecting... ===")
                val connection = connectionManager.connect()
                NSLog("=== connectWebSocket: connected, setting connection ===")
                messageSender.setConnection(connection)
                _isWebSocketConnected = true

                // Start message handler to receive responses
                messageHandler.start()

                // 認証メッセージを送信（トークンが设定されている场合）
                val authState = authService.authState.value
                NSLog("=== connectWebSocket: authState = $authState ===")
                if (authState is AuthState.Authenticated) {
                    NSLog("=== connectWebSocket: sending authentication ===")
                    authService.sendAuthentication(authState.tokens.userToken, authState.tokens.serverToken)
                    messageSender.sendQueueList()
                }

                NSLog("=== connectWebSocket: calling onConnected ===")
                onConnected()
            } catch (e: Exception) {
                NSLog("=== connectWebSocket: ERROR: ${e.message} ===")
                _isWebSocketConnected = false
                onError(e)
            }
        }
    }

    fun createAuthViewModel(): AuthViewModel {
        return AuthViewModel(authService, this)
    }

    fun createQueueListViewModel(): QueueListViewModel {
        return QueueListViewModel(queueRepository, messageSender, authService)
    }

    fun createQueueCreateViewModel(): QueueCreateViewModel {
        return QueueCreateViewModel(providerRepository, messageSender, authService)
    }

    fun createQueueDetailViewModel(): QueueDetailViewModel {
        return QueueDetailViewModel(taskRepository, messageSender, authService)
    }

    fun createProviderListViewModel(): ProviderListViewModel {
        return ProviderListViewModel(providerRepository, messageSender, authService)
    }

    fun createTaskDetailViewModel(): TaskDetailViewModel {
        return TaskDetailViewModel(taskRepository, messageSender, authService)
    }

    suspend fun getServerToken(userToken: String): String? {
        return try {
            val response = httpClient.post("${config.apiBaseUrl}api/token") {
                setBody(JsonObject(mapOf("token" to JsonPrimitive(userToken))))
                headers.append("Content-Type", "application/json")
            }
            val jsonResponse = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            jsonResponse["token"]?.jsonPrimitive?.content
        } catch (e: Exception) {
            NSLog("=== getServerToken error: ${e.message} ===")
            null
        }
    }
}
