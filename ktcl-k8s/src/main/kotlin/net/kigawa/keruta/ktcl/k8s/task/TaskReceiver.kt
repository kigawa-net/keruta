package net.kigawa.keruta.ktcl.k8s.task

import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import net.kigawa.keruta.ktcl.k8s.connection.JvmWebSocketConnection
import net.kigawa.keruta.ktcl.k8s.connection.ReceiveClientUnknownArg
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.kodel.api.log.LoggerFactory

class TaskReceiver(
    private val connection: JvmWebSocketConnection,
    private val clientEntrypoints: KtcpClientEntrypoints<ClientCtx>,
) {
    private val logger = LoggerFactory.get("TaskReceiver")

    suspend fun startReceiving(ctx: ClientCtx) = coroutineScope {
        while (isActive) {
            val message = try {
                connection.receive()
            } catch (_: ClosedReceiveChannelException) {
                logger.info { "WebSocket connection closed" }
                return@coroutineScope
            } ?: continue

            val unknownArg = ReceiveClientUnknownArg.fromText(message, ctx.serializer)
            if (unknownArg == null) {
                logger.info { "Failed to parse message type: $message" }
                continue
            }

            try {
                clientEntrypoints.access(unknownArg, ctx)?.execute()
            } catch (e: Exception) {
                logger.info { "Failed to process message: ${e.message}" }
            }
        }
    }
}
