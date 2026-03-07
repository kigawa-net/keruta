package net.kigawa.keruta.ktcl.k8s.task

import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.connection.JvmWebSocketConnection
import net.kigawa.keruta.ktcl.k8s.connection.ReceiveClientUnknownArg
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.domain.task.list.ServerTaskListMsg
import net.kigawa.kodel.api.err.unwrap
import net.kigawa.kodel.api.log.LoggerFactory
import kotlin.time.Duration.Companion.seconds

class TaskReceiver(
    private val connection: JvmWebSocketConnection,
    private val config: K8sConfig,
    private val ktcpClient: KtcpClient,
) {
    private val logger = LoggerFactory.get("TaskReceiver")

    suspend fun startReceiving(ctx: ClientCtx, userId: String): Boolean = coroutineScope {
        val taskListMsg = ServerTaskListMsg(queueId = config.queueId)
        ktcpClient.ktcpServerEntrypoints.taskList.access(taskListMsg, ctx)?.execute()
            ?: run {
                logger.severe { "Failed to send task list request for user $userId" }
                return@coroutineScope false
            }

        logger.info { "Task list requested for user $userId, queue: ${config.queueId}" }

        val message = try {
            withTimeout(10.seconds) {
                connection.receive()
            }
        } catch (_: ClosedReceiveChannelException) {
            logger.info { "WebSocket connection closed" }
            return@coroutineScope false
        } ?: return@coroutineScope false

        val unknownArg = ReceiveClientUnknownArg.fromText(message, ctx.serializer)
        if (unknownArg == null) {
            logger.info { "Failed to parse message type: $message" }
            return@coroutineScope false
        }
        val taskListed = unknownArg.tryToTaskListed()
            ?.unwrap {
                it.printStackTrace()
                return@coroutineScope false
            } ?: return@coroutineScope false
        logger.info { "Received task list: $taskListed" }
        val task = taskListed.tasks.firstOrNull { it.status != "completed" } ?: return@coroutineScope false

        return@coroutineScope true
    }
}

