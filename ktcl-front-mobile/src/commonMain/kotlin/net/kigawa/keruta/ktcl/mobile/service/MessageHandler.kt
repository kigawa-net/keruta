package net.kigawa.keruta.ktcl.mobile.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kigawa.keruta.ktcl.mobile.msg.provider.ClientProviderListMsg
import net.kigawa.keruta.ktcl.mobile.msg.queue.ClientQueueListedMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ClientTaskCreatedMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ClientTaskListedMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ClientTaskMovedMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ClientTaskUpdatedMsg
import net.kigawa.keruta.ktcl.mobile.provider.ProviderRepository
import net.kigawa.keruta.ktcl.mobile.queue.QueueRepository
import net.kigawa.keruta.ktcl.mobile.task.TaskRepository
import net.kigawa.keruta.ktcl.mobile.util.log

class MessageHandler(
    private val messageSender: MessageSender,
    private val queueRepository: QueueRepository,
    private val taskRepository: TaskRepository,
    private val providerRepository: ProviderRepository,
    private val scope: CoroutineScope,
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun start() {
        log("=== MessageHandler: start called ===")
        scope.launch {
            messageSender.connection.collect { connection ->
                log("=== MessageHandler: connection changed: ${connection != null} ===")
                if (connection != null) {
                    log("=== MessageHandler: connection established, collecting messages ===")
                    // Use the connection's messages flow
                    launch {
                        log("=== MessageHandler: about to collect messages ===")
                        connection.messages.collect { message ->
                            log("=== MessageHandler: received from flow: $message ===")
                            handleMessage(message)
                        }
                    }
                }
            }
        }
    }

    private fun handleMessage(message: String) {
        try {
            val jsonElement = json.parseToJsonElement(message)
            val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content
            log("=== MessageHandler: message type: $type ===")

            when (type) {
                "task_listed" -> {
                    val msg = json.decodeFromString<ClientTaskListedMsg>(message)
                    log("=== MessageHandler: got ${msg.tasks.size} tasks ===")
                    taskRepository.updateTasks(msg.tasks)
                }
                "task_created" -> {
                    val msg = json.decodeFromString<ClientTaskCreatedMsg>(message)
                    log("=== MessageHandler: task created: ${msg.id} ===")
                }
                "task_updated" -> {
                    val msg = json.decodeFromString<ClientTaskUpdatedMsg>(message)
                    log("=== MessageHandler: task updated: ${msg.id} ===")
                    taskRepository.updateTask(msg.id) { task ->
                        task.copy(status = msg.status)
                    }
                }
                "task_moved" -> {
                    val msg = json.decodeFromString<ClientTaskMovedMsg>(message)
                    log("=== MessageHandler: task moved: ${msg.taskId} ===")
                    taskRepository.removeTask(msg.taskId)
                }
                "queue_listed" -> {
                    val msg = json.decodeFromString<ClientQueueListedMsg>(message)
                    log("=== MessageHandler: got ${msg.queues.size} queues ===")
                    queueRepository.updateQueues(msg.queues)
                }
                "queue_created" -> {
                    log("=== MessageHandler: queue created, requesting queue list ===")
                    scope.launch {
                        messageSender.sendQueueList()
                    }
                }
                "provider_listed" -> {
                    val msg = json.decodeFromString<ClientProviderListMsg>(message)
                    log("=== MessageHandler: got ${msg.providers.size} providers ===")
                    providerRepository.updateProviders(msg.providers)
                }
                "auth_success" -> {
                    log("=== MessageHandler: authentication successful ===")
                }
                else -> log("=== MessageHandler: unknown message type: $type ===")
            }
        } catch (e: Exception) {
            log("=== MessageHandler: error: ${e.message} ===")
        }
    }
}
