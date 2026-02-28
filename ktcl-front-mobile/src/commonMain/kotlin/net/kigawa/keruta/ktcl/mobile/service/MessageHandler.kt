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
import platform.Foundation.NSLog

class MessageHandler(
    private val messageSender: MessageSender,
    private val queueRepository: QueueRepository,
    private val taskRepository: TaskRepository,
    private val providerRepository: ProviderRepository,
    private val scope: CoroutineScope,
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun start() {
        NSLog("=== MessageHandler: start called ===")
        scope.launch {
            messageSender.connection.collect { connection ->
                NSLog("=== MessageHandler: connection changed: ${connection != null} ===")
                if (connection != null) {
                    NSLog("=== MessageHandler: connection established, collecting messages ===")
                    // Use the connection's messages flow
                    launch {
                        NSLog("=== MessageHandler: about to collect messages ===")
                        connection.messages.collect { message ->
                            NSLog("=== MessageHandler: received from flow: $message ===")
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
            NSLog("=== MessageHandler: message type: $type ===")

            when (type) {
                "task_listed" -> {
                    val msg = json.decodeFromString<ClientTaskListedMsg>(message)
                    NSLog("=== MessageHandler: got ${msg.tasks.size} tasks ===")
                    taskRepository.updateTasks(msg.tasks)
                }
                "task_created" -> {
                    val msg = json.decodeFromString<ClientTaskCreatedMsg>(message)
                    NSLog("=== MessageHandler: task created: ${msg.id} ===")
                }
                "task_updated" -> {
                    val msg = json.decodeFromString<ClientTaskUpdatedMsg>(message)
                    NSLog("=== MessageHandler: task updated: ${msg.id} ===")
                    taskRepository.updateTask(msg.id) { task ->
                        task.copy(status = msg.status)
                    }
                }
                "task_moved" -> {
                    val msg = json.decodeFromString<ClientTaskMovedMsg>(message)
                    NSLog("=== MessageHandler: task moved: ${msg.taskId} ===")
                    taskRepository.removeTask(msg.taskId)
                }
                "queue_listed" -> {
                    val msg = json.decodeFromString<ClientQueueListedMsg>(message)
                    NSLog("=== MessageHandler: got ${msg.queues.size} queues ===")
                    queueRepository.updateQueues(msg.queues)
                }
                "queue_created" -> {
                    NSLog("=== MessageHandler: queue created, requesting queue list ===")
                    scope.launch {
                        messageSender.sendQueueList()
                    }
                }
                "provider_listed" -> {
                    val msg = json.decodeFromString<ClientProviderListMsg>(message)
                    NSLog("=== MessageHandler: got ${msg.providers.size} providers ===")
                    providerRepository.updateProviders(msg.providers)
                }
                "auth_success" -> {
                    NSLog("=== MessageHandler: authentication successful ===")
                }
                else -> NSLog("=== MessageHandler: unknown message type: $type ===")
            }
        } catch (e: Exception) {
            NSLog("=== MessageHandler: error: ${e.message} ===")
        }
    }
}
