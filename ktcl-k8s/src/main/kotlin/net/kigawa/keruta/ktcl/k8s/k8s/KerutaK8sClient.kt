package net.kigawa.keruta.ktcl.k8s.k8s

import net.kigawa.keruta.ktcp.client.KtcpSession
import kotlinx.coroutines.coroutineScope
import net.kigawa.keruta.ktcl.k8s.auth.AuthManager
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.connection.ConnectionManager
import net.kigawa.keruta.ktcl.k8s.connection.JvmWebSocketConnection
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveAuthSuccessEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveGenericErrEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveProviderAddTokenEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveProviderDeletedEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveProviderIdpAddedEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveProviderListedEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveQueueCreatedEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveQueueListedEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveQueueShowedEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveTaskCreatedEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveTaskListedEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveTaskMovedEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveTaskShowedEntrypoint
import net.kigawa.keruta.ktcl.k8s.entrypoint.ReceiveTaskUpdatedEntrypoint
import net.kigawa.keruta.ktcl.k8s.task.TaskExecutor
import net.kigawa.keruta.ktcl.k8s.task.TaskReceiver
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.serialize.JsonKerutaSerializer
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListMsg
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class KerutaK8sClient(
    private val config: K8sConfig,
) {
    private val logger = LoggerFactory.get("KerutaK8sClient")
    private val serializer = JsonKerutaSerializer()
    private val ktcpClient = KtcpClient()

    suspend fun start() = coroutineScope {
        logger.info { "Starting Keruta K8s Client" }

        val (connection, ctx) = connectAndCreateSession()
        if (!authenticate(ctx)) return@coroutineScope

        val taskExecutor = createTaskExecutor()
        val clientEntrypoints = createClientEntrypoints(taskExecutor)

        requestInitialTaskList(ctx)
        startMessageReceiver(connection, ctx, clientEntrypoints)
    }

    private data class ConnectionContext(
        val connection: JvmWebSocketConnection,
        val ctx: ClientCtx
    )

    private suspend fun connectAndCreateSession(): ConnectionContext {
        val connectionManager = ConnectionManager(config)
        val connection = connectionManager.connect()
        val session = KtcpSession(connection)
        val ctx = ClientCtx(serializer, session)
        return ConnectionContext(connection, ctx)
    }

    private suspend fun authenticate(ctx: ClientCtx): Boolean {
        val authManager = AuthManager(config, ktcpClient, ctx)
        return when (val authRes = authManager.authenticate()) {
            is Res.Err -> {
                logger.info { "Authentication failed: ${authRes.err}" }
                false
            }
            is Res.Ok -> {
                logger.info { "Authentication successful" }
                true
            }
        }
    }

    private fun createTaskExecutor(): TaskExecutor {
        val k8sClient = K8sClientFactory.createClient(config)
        val templateLoader = JobTemplateLoader(config.k8sJobTemplate)
        val jobExecutor = K8sJobExecutor(k8sClient, config, templateLoader)
        val jobWatcher = K8sJobWatcher(k8sClient, config)
        return TaskExecutor(jobExecutor, jobWatcher, ktcpClient)
    }

    private fun createClientEntrypoints(taskExecutor: TaskExecutor): KtcpClientEntrypoints<ClientCtx> {
        return KtcpClientEntrypoints(
            genericErrEntrypoint = ReceiveGenericErrEntrypoint(),
            authSuccessEntrypoint = ReceiveAuthSuccessEntrypoint(),
            providerListEntrypoint = ReceiveProviderListedEntrypoint(),
            providerAddTokenEntrypoint = ReceiveProviderAddTokenEntrypoint(),
            providerIdpAddedEntrypoint = ReceiveProviderIdpAddedEntrypoint(),
            providerDeletedEntrypoint = ReceiveProviderDeletedEntrypoint(),
            queueCreatedEntrypoint = ReceiveQueueCreatedEntrypoint(),
            queueListedEntrypoint = ReceiveQueueListedEntrypoint(),
            queueShowedEntrypoint = ReceiveQueueShowedEntrypoint(),
            taskCreatedEntrypoint = ReceiveTaskCreatedEntrypoint(ktcpClient, config.queueId),
            taskUpdatedEntrypoint = ReceiveTaskUpdatedEntrypoint(),
            taskMovedEntrypoint = ReceiveTaskMovedEntrypoint(),
            taskListedEntrypoint = ReceiveTaskListedEntrypoint(taskExecutor),
            taskShowedEntrypoint = ReceiveTaskShowedEntrypoint(taskExecutor)
        )
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
        clientEntrypoints: KtcpClientEntrypoints<ClientCtx>
    ) {
        val taskReceiver = TaskReceiver(connection, serializer, clientEntrypoints)
        logger.info { "Starting message receiver loop" }
        taskReceiver.startReceiving(ctx)
    }
}
