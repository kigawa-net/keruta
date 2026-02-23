package net.kigawa.keruta.ktcl.k8s.k8s

import kotlinx.coroutines.coroutineScope
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.connection.ConnectionContext
import net.kigawa.keruta.ktcl.k8s.connection.ConnectionManager
import net.kigawa.keruta.ktcl.k8s.connection.JvmWebSocketConnection
import net.kigawa.keruta.ktcl.k8s.entrypoint.ClientEntrypointsFactory
import net.kigawa.keruta.ktcl.k8s.task.TaskExecutorFactory
import net.kigawa.keruta.ktcl.k8s.task.TaskReceiver
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.client.KtcpSession
import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.auth.request.ServerAuthRequestMsg
import net.kigawa.keruta.ktcp.model.serialize.JsonKerutaSerializer
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListMsg
import net.kigawa.kodel.api.log.LoggerFactory

class KerutaK8sClient(
    private val config: K8sConfig,
) {
    private val logger = LoggerFactory.get("KerutaK8sClient")
    private val serializer = JsonKerutaSerializer()
    private val ktcpClient = KtcpClient()

    suspend fun start() = coroutineScope {
        logger.info { "Starting Keruta K8s Client" }


        logger.info { "Tokens obtained, proceeding with connection" }

        val (connection, ctx) = connectAndCreateSession()

        val taskExecutor = TaskExecutorFactory(config, ktcpClient).create()
        val clientEntrypoints = ClientEntrypointsFactory(ktcpClient, config, taskExecutor).create()


        requestInitialTaskList(ctx)
        startMessageReceiver(connection, ctx, clientEntrypoints)
    }

    private suspend fun connectAndCreateSession(): ConnectionContext {
        val connectionManager = ConnectionManager(config)
        val connection = connectionManager.connect()
        val session = KtcpSession(connection)
        val ctx = ClientCtx(serializer, session)
        return ConnectionContext(connection, ctx)
    }

    private suspend fun authenticate(ctx: ClientCtx, userToken: String, serverToken: String) {
        logger.info { "Authenticating with server" }
        val authMsg = ServerAuthRequestMsg(
            userToken = userToken,
            serverToken = serverToken,
        )
        ktcpClient.ktcpServerEntrypoints.authRequestEntrypoint.access(authMsg, ctx)?.execute()
        logger.info { "Authentication request sent" }
    }

    private suspend fun requestInitialTaskList(ctx: ClientCtx) {
        logger.info { "Requesting task list for queue ${config.queueId}" }
        ktcpClient.ktcpServerEntrypoints.taskList.access(
            ServerTaskListMsg(queueId = config.queueId),
            ctx
        )?.execute()
    }

    private suspend fun startMessageReceiver(
        connection: JvmWebSocketConnection,
        ctx: ClientCtx,
        clientEntrypoints: KtcpClientEntrypoints<ClientCtx>,
    ) {
        val taskReceiver = TaskReceiver(connection, clientEntrypoints)
        logger.info { "Starting message receiver loop" }
        taskReceiver.startReceiving(ctx)
    }
}
