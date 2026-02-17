package net.kigawa.keruta.ktcl.mobile.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kigawa.keruta.ktcl.mobile.connection.MobileKtcpConnection
import net.kigawa.keruta.ktcl.mobile.msg.queue.ClientQueueCreatedMsg
import net.kigawa.keruta.ktcl.mobile.msg.queue.ClientQueueListedMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ClientTaskCreatedMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ClientTaskListedMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ClientTaskMovedMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ClientTaskUpdatedMsg
import net.kigawa.keruta.ktcl.mobile.provider.ProviderRepository
import net.kigawa.keruta.ktcl.mobile.queue.QueueRepository

class TaskReceiver(
    private val connection: MobileKtcpConnection,
    private val taskRepository: TaskRepository,
    private val queueRepository: QueueRepository,
    private val providerRepository: ProviderRepository,
    private val scope: CoroutineScope,
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun startReceiving() {
        scope.launch {
            while (true) {
                val message = connection.receive() ?: continue
                handleMessage(message)
            }
        }
    }

    private fun handleMessage(message: String) {
        try {
            val jsonElement = json.parseToJsonElement(message)
            val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content

            when (type) {
                "task_listed" -> {
                    val msg = json.decodeFromString<ClientTaskListedMsg>(message)
                    taskRepository.updateTasks(msg.tasks)
                }
                "task_created" -> {
                    val msg = json.decodeFromString<ClientTaskCreatedMsg>(message)
                    println("タスク作成成功: ID=${msg.id}")
                }
                "task_updated" -> {
                    val msg = json.decodeFromString<ClientTaskUpdatedMsg>(message)
                    taskRepository.updateTask(msg.id) { task ->
                        task.copy(status = msg.status)
                    }
                }
                "task_moved" -> {
                    val msg = json.decodeFromString<ClientTaskMovedMsg>(message)
                    taskRepository.removeTask(msg.taskId)
                }
                "queue_created" -> {
                    val msg = json.decodeFromString<ClientQueueCreatedMsg>(message)
                    println("キュー作成成功: ID=${msg.queueId}")
                }
                "queue_listed" -> {
                    val msg = json.decodeFromString<ClientQueueListedMsg>(message)
                    queueRepository.updateQueues(msg.queues)
                }
                "provider_listed" -> {
                    val msg = json.decodeFromString<net.kigawa.keruta.ktcl.mobile.msg.provider.ClientProviderListMsg>(message)
                    providerRepository.updateProviders(msg.providers)
                }
                else -> println("不明なメッセージタイプ: $type")
            }
        } catch (e: Exception) {
            println("メッセージ処理エラー: ${e.message}")
        }
    }
}
