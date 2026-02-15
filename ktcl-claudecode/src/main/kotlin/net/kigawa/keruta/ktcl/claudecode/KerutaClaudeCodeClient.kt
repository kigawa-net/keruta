package net.kigawa.keruta.ktcl.claudecode

import kotlinx.coroutines.coroutineScope
import net.kigawa.keruta.ktcl.claudecode.auth.AuthManager
import net.kigawa.keruta.ktcl.claudecode.claude.ClaudeCodeCliClient
import net.kigawa.keruta.ktcl.claudecode.config.ClaudeCodeConfig
import net.kigawa.keruta.ktcl.claudecode.connection.ConnectionManager
import net.kigawa.keruta.ktcl.claudecode.entrypoint.*
import net.kigawa.keruta.ktcl.claudecode.task.TaskExecutor
import net.kigawa.keruta.ktcl.claudecode.task.TaskReceiver
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.client.KtcpSession
import net.kigawa.keruta.ktcp.model.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.model.serialize.JsonKerutaSerializer
import net.kigawa.keruta.ktcp.model.task.list.ServerTaskListMsg
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory as KodelLoggerFactory

class KerutaClaudeCodeClient(
    private val config: ClaudeCodeConfig,
) {
    private val logger = KodelLoggerFactory.get("KerutaClaudeCodeClient")
    private val serializer = JsonKerutaSerializer()
    private val claudeClient = ClaudeCodeCliClient()
    private val ktcpClient = KtcpClient()

    suspend fun start() = coroutineScope {
        logger.info { "Starting Keruta Claude Code Client" }

        // WebSocket接続
        val connectionManager = ConnectionManager(config)
        val connection = connectionManager.connect()

        val session = KtcpSession(connection)
        val ctx = ClientCtx(serializer, session)

        // 認証
        val authManager = AuthManager(config, ktcpClient, ctx)
        when (val authRes = authManager.authenticate()) {
            is Res.Err -> {
                logger.info { "Authentication failed: ${authRes.err}" }
                return@coroutineScope
            }
            is Res.Ok -> logger.info { "Authentication successful" }
        }

        // タスク実行エンジン
        val taskExecutor = TaskExecutor(claudeClient, ktcpClient)

        // クライアントエントリーポイント設定
        val clientEntrypoints = KtcpClientEntrypoints(
            genericErrEntrypoint = ReceiveGenericErrEntrypoint(),
            authSuccessEntrypoint = ReceiveAuthSuccessEntrypoint(),
            providerListEntrypoint = ReceiveProviderListedEntrypoint(),
            providerAddTokenEntrypoint = ReceiveProviderAddTokenEntrypoint(),
            providerIdpAddedEntrypoint = ReceiveProviderIdpAddedEntrypoint(),
            queueCreatedEntrypoint = ReceiveQueueCreatedEntrypoint(),
            queueListedEntrypoint = ReceiveQueueListedEntrypoint(),
            queueShowedEntrypoint = ReceiveQueueShowedEntrypoint(),
            taskCreatedEntrypoint = ReceiveTaskCreatedEntrypoint(ktcpClient, config.queueId),
            taskUpdatedEntrypoint = ReceiveTaskUpdatedEntrypoint(),
            taskMovedEntrypoint = ReceiveTaskMovedEntrypoint(),
            taskListedEntrypoint = ReceiveTaskListedEntrypoint(taskExecutor),
            taskShowedEntrypoint = ReceiveTaskShowedEntrypoint(taskExecutor)
        )

        // 起動時に既存のpendingタスクを確認
        logger.info { "Requesting task list for queue ${config.queueId}" }
        ktcpClient.ktcpServerEntrypoints.taskList.access(
            ServerTaskListMsg(queueId = config.queueId),
            ctx
        )?.execute()

        // メッセージ受信ループ
        val taskReceiver = TaskReceiver(connection, serializer, clientEntrypoints)
        logger.info { "Starting message receiver loop" }
        taskReceiver.startReceiving(ctx)
    }
}

suspend fun main() {
    val config = ClaudeCodeConfig.fromEnvironment()
    val client = KerutaClaudeCodeClient(config)
    client.start()
}
