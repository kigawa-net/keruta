package net.kigawa.keruta.ktcl.mobile.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.kigawa.keruta.ktcl.mobile.connection.MobileKtcpConnection
import net.kigawa.keruta.ktcl.mobile.msg.provider.ServerProviderCreateMsg
import net.kigawa.keruta.ktcl.mobile.msg.provider.ServerProviderListMsg
import net.kigawa.keruta.ktcl.mobile.msg.queue.ServerQueueCreateMsg
import net.kigawa.keruta.ktcl.mobile.msg.queue.ServerQueueListMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ServerTaskCreateMsg
import net.kigawa.keruta.ktcl.mobile.msg.task.ServerTaskListMsg

class MessageSender {
    private val _connection = MutableStateFlow<MobileKtcpConnection?>(null)
    val connection: StateFlow<MobileKtcpConnection?> = _connection.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    fun setConnection(connection: MobileKtcpConnection?) {
        _connection.value = connection
    }

    suspend fun sendQueueList() {
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

    suspend fun sendProviderCreate(name: String, issuer: String, audience: String) {
        val msg = ServerProviderCreateMsg(name = name, issuer = issuer, audience = audience)
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

    private suspend fun sendMessage(message: String) {
        _connection.value?.send(message)
    }
}
