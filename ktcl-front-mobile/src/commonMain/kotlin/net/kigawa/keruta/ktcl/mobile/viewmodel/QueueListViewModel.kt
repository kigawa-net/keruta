package net.kigawa.keruta.ktcl.mobile.viewmodel

import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.msg.queue.Queue
import net.kigawa.keruta.ktcl.mobile.queue.QueueRepository
import net.kigawa.keruta.ktcl.mobile.util.log

data class QueueListViewState(
    val queues: List<Queue> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class QueueListViewModel(
    private val queueRepository: QueueRepository,
): BaseViewModel<QueueListViewState>(QueueListViewState(isLoading = true)) {

    init {
        viewModelScope.launch {
            try {

                queueRepository.queues.collect { queues ->
                    log("=== QueueListViewModel: queueRepository.queues.collect called ===")
                    if (queues == null) return@collect
                    updateState { it.copy(queues = queues, isLoading = false) }
                    log("=== QueueListViewModel: queues: ${queues.size} ===")
                }
            } catch (t: Throwable) {
                log("=== QueueListViewModel: error: ${t.message} ===")
                throw t
            }
        }
    }

}
