package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.auth.AuthState
import net.kigawa.keruta.ktcl.mobile.msg.queue.Queue
import net.kigawa.keruta.ktcl.mobile.queue.QueueRepository
import net.kigawa.keruta.ktcl.mobile.service.AuthService
import net.kigawa.keruta.ktcl.mobile.service.MessageSender
import platform.Foundation.NSLog

data class QueueListViewState(
    val queues: List<Queue> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class QueueListViewModel(
    private val queueRepository: QueueRepository,
    private val messageSender: MessageSender,
    private val authService: AuthService,
): BaseViewModel<QueueListViewState>(QueueListViewState(isLoading = true)) {

    init {
        viewModelScope.launch {
            try {

                queueRepository.queues.collect { queues ->
                    NSLog("=== QueueListViewModel: queueRepository.queues.collect called ===")
                    if (queues == null) return@collect
                    updateState { it.copy(queues = queues, isLoading = false) }
                    NSLog("=== QueueListViewModel: queues: ${queues.size} ===")
                }
            } catch (t: Throwable) {
                NSLog("=== QueueListViewModel: error: ${t.message} ===")
                throw t
            }
        }
    }

    fun loadQueues() {
        NSLog("=== loadQueues called ===")
        viewModelScope.launch {
            try {
                // 接続が確立されるまで待つ
                NSLog("=== loadQueues: waiting for connection ===")
                val connection = messageSender.connection.first { it != null }
                if (connection == null) {
                    NSLog("=== loadQueues: connection is null ===")
                    updateState { it.copy(isLoading = false, errorMessage = "WebSocket接続に失敗しました") }
                    return@launch
                }

                NSLog("=== loadQueues: connection exists ===")

                // 認証が完了するまで待つ
                NSLog("=== loadQueues: waiting for auth ===")
                val authState = authService.authState.first { it is AuthState.Authenticated }
                if (authState !is AuthState.Authenticated) {
                    NSLog("=== loadQueues: auth failed ===")
                    updateState { it.copy(isLoading = false, errorMessage = "認証に失敗しました") }
                    return@launch
                }

                NSLog("=== loadQueues: sending queue list request ===")
                messageSender.sendQueueList()
                NSLog("=== loadQueues: request sent ===")
            } catch (e: Exception) {
                NSLog("=== loadQueues: error: ${e.message} ===")
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
