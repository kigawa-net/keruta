package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.msg.task.Task
import net.kigawa.keruta.ktcl.mobile.service.MessageSender
import net.kigawa.keruta.ktcl.mobile.task.TaskRepository

data class QueueDetailViewState(
    val queueId: Long = 0,
    val tasks: List<Task> = emptyList(),
    val newTaskTitle: String = "",
    val newTaskDescription: String = "",
    val isLoading: Boolean = false,
    val isCreatingTask: Boolean = false,
    val errorMessage: String? = null,
)

class QueueDetailViewModel(
    private val taskRepository: TaskRepository,
    private val messageSender: MessageSender,
) : BaseViewModel<QueueDetailViewState>(QueueDetailViewState()) {

    init {
        viewModelScope.launch {
            taskRepository.tasks.collect { tasks ->
                updateState { it.copy(tasks = tasks, isLoading = false) }
            }
        }
    }

    fun setQueueId(queueId: Long) {
        updateState { it.copy(queueId = queueId) }
    }

    fun loadTasks() {
        val queueId = _state.value.queueId
        if (queueId == 0L) return

        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            try {
                messageSender.sendTaskList(queueId)
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun setNewTaskTitle(title: String) {
        updateState { it.copy(newTaskTitle = title) }
    }

    fun setNewTaskDescription(description: String) {
        updateState { it.copy(newTaskDescription = description) }
    }

    fun createTask() {
        val currentState = _state.value
        val queueId = currentState.queueId
        val title = currentState.newTaskTitle
        val description = currentState.newTaskDescription

        if (title.isBlank()) {
            updateState { it.copy(errorMessage = "タイトルを入力してください") }
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isCreatingTask = true, errorMessage = null) }
            try {
                messageSender.sendTaskCreate(queueId, title, description)
                updateState {
                    it.copy(
                        isCreatingTask = false,
                        newTaskTitle = "",
                        newTaskDescription = "",
                    )
                }
            } catch (e: Exception) {
                updateState { it.copy(isCreatingTask = false, errorMessage = e.message) }
            }
        }
    }
}
