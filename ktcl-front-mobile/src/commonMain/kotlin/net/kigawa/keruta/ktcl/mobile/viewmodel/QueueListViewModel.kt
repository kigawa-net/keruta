package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.msg.queue.Queue
import net.kigawa.keruta.ktcl.mobile.queue.QueueRepository
import net.kigawa.keruta.ktcl.mobile.service.MessageSender

data class QueueListViewState(
    val queues: List<Queue> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class QueueListViewModel(
    private val queueRepository: QueueRepository,
    private val messageSender: MessageSender,
) : BaseViewModel<QueueListViewState>(QueueListViewState()) {

    init {
        viewModelScope.launch {
            queueRepository.queues.collect { queues ->
                updateState { it.copy(queues = queues, isLoading = false) }
            }
        }
    }

    fun loadQueues() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            try {
                messageSender.sendQueueList()
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
