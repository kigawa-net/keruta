package net.kigawa.keruta.ktcl.mobile.queue

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.kigawa.keruta.ktcl.mobile.msg.queue.Queue

class QueueRepository {
    private val _queues = MutableStateFlow<List<Queue>>(emptyList())
    val queues: StateFlow<List<Queue>> = _queues.asStateFlow()

    fun updateQueues(newQueues: List<Queue>) {
        _queues.value = newQueues
    }

    fun addQueue(queue: Queue) {
        _queues.value = _queues.value + queue
    }
}
