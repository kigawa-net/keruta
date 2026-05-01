package net.kigawa.keruta.ktcl.mobile.queue

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.kigawa.keruta.ktcl.mobile.msg.queue.Queue

class QueueRepository {
    private val _queues = MutableStateFlow<List<Queue>?>(null)
    val queues: StateFlow<List<Queue>?> = _queues.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Main).launch {
            println("=== QueueRepository: init called ===")
            _queues.collect {
                println("=== QueueRepository: queues: ${it?.size} ===")
            }
            println("=== QueueRepository: init called ===")
        }
    }

    fun updateQueues(newQueues: List<Queue>) {
        println("=== QueueRepository: updateQueues called ===")
        _queues.value = newQueues
    }

}
