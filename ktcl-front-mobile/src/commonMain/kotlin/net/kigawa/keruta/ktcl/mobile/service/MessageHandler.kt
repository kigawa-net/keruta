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

class MessageHandler(
    private val messageSender: MessageSender,
    private val queueRepository: QueueRepository,
    private val taskRepository: TaskRepository,
    private val providerRepository: ProviderRepository,
    private val scope: CoroutineScope,
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun start() {
        scope.launch {
            messageSender.connection.collect { connection ->
                if (connection != null) {
                    println("=== MessageHandler: connection established, collecting messages ===")
                    // Use the connection's messages flow
                    launch {
                        connection.messages.collect { message ->
                            println("=== MessageHandler: received from flow: $message ===")
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
            println("=== MessageHandler: message type: $type ===")

            when (type) {
                "task_listed" -> {
                    val msg = json.decodeFromString<ClientTaskListedMsg>(message)
                    println("=== MessageHandler: got ${msg.tasks.size} tasks ===")
                    taskRepository.updateTasks(msg.tasks)
                }
                "task_created" -> {
                    val msg = json.decodeFromString<ClientTaskCreatedMsg>(message)
                    println("=== MessageHandler: task created: ${msg.id} ===")
                }
                "task_updated" -> {
                    val msg = json.decodeFromString<ClientTaskUpdatedMsg>(message)
                    println("=== MessageHandler: task updated: ${msg.id} ===")
                    taskRepository.updateTask(msg.id) { task ->
                        task.copy(status = msg.status)
                    }
                }
                "task_moved" -> {
                    val msg = json.decodeFromString<ClientTaskMovedMsg>(message)
                    println("=== MessageHandler: task moved: ${msg.taskId} ===")
                    taskRepository.removeTask(msg.taskId)
                }
                "queue_listed" -> {
                    val msg = json.decodeFromString<ClientQueueListedMsg>(message)
                    println("=== MessageHandler: got ${msg.queues.size} queues ===")
                    queueRepository.updateQueues(msg.queues)
                }
                "provider_listed" -> {
                    val msg = json.decodeFromString<ClientProviderListMsg>(message)
                    println("=== MessageHandler: got ${msg.providers.size} providers ===")
                    providerRepository.updateProviders(msg.providers)
                }
                "auth_success" -> {
                    println("=== MessageHandler: authentication successful ===")
                }
                else -> println("=== MessageHandler: unknown message type: $type ===")
            }
        } catch (e: Exception) {
            println("=== MessageHandler: error: ${e.message} ===")
        }
    }
}
