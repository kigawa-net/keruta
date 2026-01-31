package net.kigawa.keruta.ktcl.k8s

import KtcpSession
import kotlinx.coroutines.coroutineScope
import net.kigawa.keruta.ktcl.k8s.auth.AuthManager
import net.kigawa.keruta.ktcl.k8s.config.K8sConfig
import net.kigawa.keruta.ktcl.k8s.connection.ConnectionManager
import net.kigawa.keruta.ktcl.k8s.entrypoint.*
import net.kigawa.keruta.ktcl.k8s.k8s.JobTemplateLoader
import net.kigawa.keruta.ktcl.k8s.k8s.K8sClientFactory
import net.kigawa.keruta.ktcl.k8s.k8s.K8sJobExecutor
import net.kigawa.keruta.ktcl.k8s.k8s.K8sJobWatcher
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

        // 1. WebSocket接続
        val connectionManager = ConnectionManager(config)
        val connection = connectionManager.connect()

        val session = KtcpSession(connection)
        val ctx = ClientCtx(serializer, session)

        // 2. KTCP認証
        val authManager = AuthManager(config, ktcpClient, ctx)
        when (val authRes = authManager.authenticate()) {
            is Res.Err -> {
                logger.info { "Authentication failed: ${authRes.err}" }
                return@coroutineScope
            }

            is Res.Ok -> logger.info { "Authentication successful" }
        }

        // 3. K8s Client初期化
        val k8sClient = K8sClientFactory.createClient(config)

        // 4. JobTemplateLoader初期化
        val templateLoader = JobTemplateLoader(config.k8sJobTemplate)

        // 5. K8s Job実行エンジン
        val jobExecutor = K8sJobExecutor(k8sClient, config, templateLoader)
        val jobWatcher = K8sJobWatcher(k8sClient, config)

        // 6. TaskExecutor初期化
        val taskExecutor = TaskExecutor(jobExecutor, jobWatcher, ktcpClient)

        // 7. エントリーポイント設定
        val clientEntrypoints = KtcpClientEntrypoints(
            genericErrEntrypoint = ReceiveGenericErrEntrypoint(),
            authSuccessEntrypoint = ReceiveAuthSuccessEntrypoint(),
            providerListEntrypoint = ReceiveProviderListedEntrypoint(),
            queueCreatedEntrypoint = ReceiveQueueCreatedEntrypoint(),
            queueListedEntrypoint = ReceiveQueueListedEntrypoint(),
            queueShowedEntrypoint = ReceiveQueueShowedEntrypoint(),
            taskCreatedEntrypoint = ReceiveTaskCreatedEntrypoint(ktcpClient, config.queueId),
            taskUpdatedEntrypoint = ReceiveTaskUpdatedEntrypoint(),
            taskMovedEntrypoint = ReceiveTaskMovedEntrypoint(),
            taskListedEntrypoint = ReceiveTaskListedEntrypoint(taskExecutor),
            taskShowedEntrypoint = ReceiveTaskShowedEntrypoint(taskExecutor)
        )

        // 8. 起動時のタスク一覧取得
        logger.info { "Requesting task list for queue ${config.queueId}" }
        ktcpClient.ktcpServerEntrypoints.taskList.access(
            ServerTaskListMsg(queueId = config.queueId),
            ctx
        )?.execute()

        // 9. メッセージ受信ループ
        val taskReceiver = TaskReceiver(connection, serializer, clientEntrypoints)
        logger.info { "Starting message receiver loop" }
        taskReceiver.startReceiving(ctx)
    }
}
