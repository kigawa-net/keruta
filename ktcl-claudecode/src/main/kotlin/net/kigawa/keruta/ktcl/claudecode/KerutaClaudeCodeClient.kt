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
import net.kigawa.keruta.ktcp.domain.KtcpClientEntrypoints
import net.kigawa.keruta.ktcp.domain.task.list.ServerTaskListMsg
import net.kigawa.keruta.ktcp.usecase.JsonKerutaSerializer
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LogLevel
import net.kigawa.kodel.api.log.LogRow
import net.kigawa.kodel.api.log.LoggerFactory
import net.kigawa.kodel.api.log.config.formatter.LoggerFormatter
import net.kigawa.kodel.api.log.handler.StdHandler
import net.kigawa.kodel.api.log.LoggerFactory as KodelLoggerFactory

class KerutaClaudeCodeClient(
    private val config: ClaudeCodeConfig,
) {

    init {
        LoggerFactory.configure {
            level = LogLevel.INFO
            handler(::StdHandler) {
                level = LogLevel.DEBUG
                formatter = object: LoggerFormatter {

                    val MAX_PACKAGE_SECTION_LENGTH = 40

                    override fun format(row: LogRow): String {
                        return row.run {
                            val lvStr = level.name.padEnd(8)
                            val className = formatClassName(sourceClassName)
                            val method = sourceMethodName
                                .take(15)
                                .padEnd(15)

                            "${lvStr}[${className} #${method}]: ${message}\n"
                        }
                    }


                    private fun formatClassName(className: String): String {
                        val packageSections = className
                            .split(".")
                            .toMutableList()
                        var size = className.length
                        var index = 0
                        var prefix = ""
                        while (
                            size > MAX_PACKAGE_SECTION_LENGTH && index < packageSections.size - 1
                        ) {
                            val section = packageSections[index]
                            size -= section.length - 2
                            packageSections[index] = section.take(1)
                            index++
                        }
                        if (size > MAX_PACKAGE_SECTION_LENGTH) {
                            size++
                            prefix = "."
                        }
                        while (size > MAX_PACKAGE_SECTION_LENGTH && packageSections.size > 1) {
                            packageSections.removeFirst()
                            size -= 2
                        }

                        return packageSections
                            .joinToString(".", prefix)
                            .takeLast(MAX_PACKAGE_SECTION_LENGTH)
                            .padStart(MAX_PACKAGE_SECTION_LENGTH)
                    }
                }
            }

            child("net.kigawa") {
                level = LogLevel.DEBUG

                child("kodel") {
//                        level = LogLevel.DEBUG
                }
            }
        }
    }

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
            providerDeletedEntrypoint = ReceiveProviderDeletedEntrypoint(),
            queueCreatedEntrypoint = ReceiveQueueCreatedEntrypoint(),
            queueListedEntrypoint = ReceiveQueueListedEntrypoint(),
            queueShowedEntrypoint = ReceiveQueueShowedEntrypoint(),
            queueUpdatedEntrypoint = ReceiveQueueUpdatedEntrypoint(),
            queueDeletedEntrypoint = ReceiveQueueDeletedEntrypoint(),
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
