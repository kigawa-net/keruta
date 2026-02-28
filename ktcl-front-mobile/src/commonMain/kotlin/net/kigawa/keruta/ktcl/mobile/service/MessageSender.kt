package net.kigawa.keruta.ktcl.mobile.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import net.kigawa.keruta.ktcl.mobile.connection.MobileKtcpConnection
import net.kigawa.keruta.ktcl.mobile.msg.provider.ServerProviderListMsg
import net.kigawa.keruta.ktcl.mobile.msg.queue.ServerQueueCreateMsg
import net.kigawa.keruta.ktcl.mobile.msg.queue.ServerQueueListMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ServerTaskCreateMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ServerTaskListMsg
import platform.Foundation.NSLog

class MessageSender {
    private val _connection = MutableStateFlow<MobileKtcpConnection?>(null)
    val connection: StateFlow<MobileKtcpConnection?> = _connection.asStateFlow()

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun setConnection(connection: MobileKtcpConnection?) {
        NSLog("=== MessageSender: setConnection called ===")
        _connection.value = connection
    }

    suspend fun sendQueueList() {
        NSLog("=== MessageSender: sendQueueList called ===")
        val msg = ServerQueueListMsg()
        sendMessage(json.encodeToString(msg))
    }

    suspend fun sendQueueCreate(providerId: Long, name: String) {
        val msg = ServerQueueCreateMsg(providerId = providerId, name = name)
        sendMessage(json.encodeToString(msg))
    }

    suspend fun sendProviderList() {
        val msg = ServerProviderListMsg()
        sendMessage(json.encodeToString(msg))
    }

    suspend fun sendTaskList(queueId: Long) {
        val msg = ServerTaskListMsg(queueId = queueId)
        sendMessage(json.encodeToString(msg))
    }

    suspend fun sendTaskCreate(queueId: Long, title: String, description: String) {
        val msg = ServerTaskCreateMsg(queueId = queueId, title = title, description = description)
        sendMessage(json.encodeToString(msg))
    }

    suspend fun sendTaskUpdate(taskId: Long, status: String) {
        // KTVCPモデルに合わせてtaskIdとstatusのみを送信
        val msg = """{"type":"task_update","taskId":$taskId,"status":"$status"}"""
        sendMessage(msg)
    }

    private suspend fun sendMessage(message: String) {
        NSLog("=== MessageSender: sendMessage: $message ===")
        _connection.value?.send(message)
    }
}
