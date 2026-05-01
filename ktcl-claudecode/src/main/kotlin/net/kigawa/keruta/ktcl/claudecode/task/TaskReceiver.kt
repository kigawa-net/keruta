package net.kigawa.keruta.ktcl.claudecode.task

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kigawa.keruta.ktcl.claudecode.connection.JvmWebSocketConnection
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.domain.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.domain.msg.client.ClientMsgType
import net.kigawa.keruta.ktcp.domain.msg.server.ServerMsgType
import net.kigawa.keruta.ktcp.domain.serialize.KerutaSerializer
import net.kigawa.keruta.ktcp.domain.serialize.deserialize
import net.kigawa.keruta.ktcp.domain.task.created.ClientTaskCreatedMsg
import net.kigawa.keruta.ktcp.domain.task.listed.ClientTaskListedMsg
import net.kigawa.keruta.ktcp.domain.task.showed.ClientTaskShowedMsg
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class TaskReceiver(
    private val connection: JvmWebSocketConnection,
    private val serializer: KerutaSerializer,
    private val clientEntrypoints: KtcpClientEntrypoints<ClientCtx>,
) {
    private val logger = LoggerFactory.get("TaskReceiver")

    suspend fun startReceiving(ctx: ClientCtx) = coroutineScope {
        while (isActive) {
            val message = connection.receive() ?: break

            try {
                // JSONから型を判別
                val jsonElement = Json.parseToJsonElement(message)
                if (jsonElement is JsonObject) {
                    when (val type = jsonElement["type"]?.jsonPrimitive?.content) {
                        ClientMsgType.TASK_CREATED.str -> {
                            when (val msg = serializer.deserialize<ClientTaskCreatedMsg>(message)) {
                                is Res.Ok -> clientEntrypoints.taskCreated.access(msg.value, ctx)?.execute()
                                is Res.Err -> logger.info { "Failed to parse ClientTaskCreatedMsg: ${msg.err}" }
                            }
                        }

                        ClientMsgType.TASK_LISTED.str -> {
                            when (val msg = serializer.deserialize<ClientTaskListedMsg>(message)) {
                                is Res.Ok -> clientEntrypoints.taskListed.access(msg.value, ctx)?.execute()
                                is Res.Err -> logger.info { "Failed to parse ClientTaskListedMsg: ${msg.err}" }
                            }
                        }

                        ClientMsgType.TASK_SHOWED.str -> {
                            when (val msg = serializer.deserialize<ClientTaskShowedMsg>(message)) {
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
