package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.auth.AuthState
import net.kigawa.keruta.ktcl.mobile.msg.task.Task
import net.kigawa.keruta.ktcl.mobile.service.AuthService
import net.kigawa.keruta.ktcl.mobile.service.MessageSender
import net.kigawa.keruta.ktcl.mobile.task.TaskRepository

data class TaskDetailViewState(
    val taskId: Long = 0,
    val queueId: Long = 0,
    val task: Task? = null,
    val editTitle: String = "",
    val editDescription: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
)

class TaskDetailViewModel(
    private val taskRepository: TaskRepository,
    private val messageSender: MessageSender,
    private val authService: AuthService,
) : BaseViewModel<TaskDetailViewState>(TaskDetailViewState()) {

    private val scope = viewModelScope

    init {
        scope.launch {
            taskRepository.tasks.collect { tasks ->
                val task = tasks.find { it.id == _state.value.taskId }
                if (task != null) {
                    updateState {
                        it.copy(
                            task = task,
                            editTitle = task.title,
                            editDescription = task.description,
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }

    fun setTaskId(taskId: Long, queueId: Long) {
        updateState { it.copy(taskId = taskId, queueId = queueId, isLoading = true) }
        // 既存のタスクを探す
        val existingTask = taskRepository.tasks.value.find { it.id == taskId }
        if (existingTask != null) {
            updateState {
                it.copy(
                    task = existingTask,
                    editTitle = existingTask.title,
                    editDescription = existingTask.description,
                    isLoading = false,
                )
            }
        }
    }

    fun setEditTitle(title: String) {
        updateState { it.copy(editTitle = title) }
    }

    fun setEditDescription(description: String) {
        updateState { it.copy(editDescription = description) }
    }

    fun saveTask() {
        val currentState = _state.value
        val taskId = currentState.taskId

        if (currentState.editTitle.isBlank()) {
            updateState { it.copy(errorMessage = "タイトルを入力してください") }
            return
        }

        scope.launch {
            try {
                updateState { it.copy(isSaving = true, errorMessage = null) }

                // 接続が確立されるまで待つ
                val connection = messageSender.connection.first { it != null }
                if (connection == null) {
                    updateState { it.copy(isSaving = false, errorMessage = "WebSocket接続に失敗しました") }
                    return@launch
                }

                // 認証が完了するまで待つ
                val authState = authService.authState.first { it is AuthState.Authenticated }
                if (authState !is AuthState.Authenticated) {
                    updateState { it.copy(isSaving = false, errorMessage = "認証に失敗しました") }
                    return@launch
                }

                // タスク更新メッセージを送信
                // 注: KTVCPモデルはstatusのみサポートしているため、タイトル/説明の変更はここでは無視される
                val currentStatus = currentState.task?.status ?: "pending"
                messageSender.sendTaskUpdate(taskId, currentStatus)
                updateState { it.copy(isSaving = false) }
            } catch (e: Exception) {
                updateState { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    fun updateStatus(newStatus: String) {
        val taskId = _state.value.taskId

        scope.launch {
            try {
                updateState { it.copy(errorMessage = null) }

                // 接続が確立されるまで待つ
                val connection = messageSender.connection.first { it != null }
                if (connection == null) {
                    updateState { it.copy(errorMessage = "WebSocket接続に失敗しました") }
                    return@launch
                }

                // 認証が完了するまで待つ
                val authState = authService.authState.first { it is AuthState.Authenticated }
                if (authState !is AuthState.Authenticated) {
                    updateState { it.copy(errorMessage = "認証に失敗しました") }
                    return@launch
                }

                // タスク更新メッセージを送信
                messageSender.sendTaskUpdate(taskId, newStatus)

                // ステータスを即時更新（楽観的更新）
                updateState {
                    it.copy(task = it.task?.copy(status = newStatus))
                }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = e.message) }
            }
        }
    }

}
