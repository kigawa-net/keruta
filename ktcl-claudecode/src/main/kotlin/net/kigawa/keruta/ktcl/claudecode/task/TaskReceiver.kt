package net.kigawa.keruta.ktcl.claudecode.task

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import net.kigawa.keruta.ktcl.claudecode.connection.JvmWebSocketConnection
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsg
import net.kigawa.keruta.ktcp.model.msg.client.ClientMsgType
import net.kigawa.keruta.ktcp.model.serialize.KerutaSerializer
import net.kigawa.keruta.ktcp.model.serialize.deserialize
import net.kigawa.keruta.ktcp.model.task.created.ClientTaskCreatedMsg
import net.kigawa.keruta.ktcp.model.task.listed.ClientTaskListedMsg
import net.kigawa.keruta.ktcp.model.task.showed.ClientTaskShowedMsg
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kigawa.keruta.ktcp.model.msg.server.ServerMsgType

class TaskReceiver(
    private val connection: JvmWebSocketConnection,
    private val serializer: KerutaSerializer,
    private val clientEntrypoints: KtcpClientEntrypoints<ClientCtx>,
) {
    private val logger = LoggerFactory.get("TaskReceiver")

    suspend fun startReceiving(ctx: ClientCtx) = coroutineScope {
        while (isActive) {
            val message = connection.receive() ?: continue

            try {
                // JSONから型を判別
                val jsonElement = Json.parseToJsonElement(message)
                if (jsonElement is JsonObject) {
                    val type = jsonElement["type"]?.jsonPrimitive?.content
                    when (type) {
                        ClientMsgType.TASK_CREATED.str -> {
                            val msg = serializer.deserialize<ClientTaskCreatedMsg>(message)
                            when (msg) {
                                is Res.Ok -> clientEntrypoints.taskCreated.access(msg.value, ctx)?.execute()
                                is Res.Err -> logger.info { "Failed to parse ClientTaskCreatedMsg: ${msg.err}" }
                            }
                        }
                        ClientMsgType.TASK_LISTED.str -> {
                            val msg = serializer.deserialize<ClientTaskListedMsg>(message)
                            when (msg) {
                                is Res.Ok -> clientEntrypoints.taskListed.access(msg.value, ctx)?.execute()
                                is Res.Err -> logger.info { "Failed to parse ClientTaskListedMsg: ${msg.err}" }
                            }
                        }
                        ClientMsgType.TASK_SHOWED.str -> {
                            val msg = serializer.deserialize<ClientTaskShowedMsg>(message)
                            when (msg) {
                                is Res.Ok -> clientEntrypoints.taskShowed.access(msg.value, ctx)?.execute()
                                is Res.Err -> logger.info { "Failed to parse ClientTaskShowedMsg: ${msg.err}" }
                            }
                        }
                        ServerMsgType.AUTH_SUCCESS.str -> {
                            // 認証成功メッセージは特に処理しない（ログのみ）
                            logger.info { "Authentication success confirmed" }
                        }
                        else -> {
                            logger.info { "Unknown message type: $type" }
                        }
                    }
                }
            } catch (e: Exception) {
                logger.info { "Failed to process message: ${e.message}" }
            }
        }
    }
}