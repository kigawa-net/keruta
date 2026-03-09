package net.kigawa.keruta.ktcl.k8s.task

import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.kigawa.keruta.ktcl.k8s.connection.JvmWebSocketConnection
import net.kigawa.keruta.ktcl.k8s.connection.ReceiveClientUnknownArg
import net.kigawa.keruta.ktcl.k8s.k8s.K8sJobExecutor
import net.kigawa.keruta.ktcl.k8s.persist.dao.UserTokenDao
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.domain.provider.list.ServerProviderListMsg
import net.kigawa.keruta.ktcp.domain.queue.list.ServerQueueListMsg
import net.kigawa.keruta.ktcp.domain.queue.show.ServerQueueShowMsg
import net.kigawa.keruta.ktcp.domain.task.list.ServerTaskListMsg
import net.kigawa.kodel.api.err.unwrap
import net.kigawa.kodel.api.log.LoggerFactory
import kotlin.time.Duration.Companion.seconds

class TaskReceiver(
    private val connection: JvmWebSocketConnection,
    private val ktcpClient: KtcpClient,
    private val jobExecutor: K8sJobExecutor,
    private val ktclIssuer: String,
    private val userTokenDao: UserTokenDao,
) {
    private val logger = LoggerFactory.get("TaskReceiver")

    suspend fun startReceiving(ctx: ClientCtx, userSubject: String, userIssuer: String): Boolean = coroutineScope {
        // 0. auth_success待ち
        val authMsg = receiveMsg(ctx) ?: return@coroutineScope false
        if (authMsg.tryToAuthSuccess() == null) {
            logger.severe { "Expected auth_success but got different message for user $userSubject" }
            return@coroutineScope false
        }

        // 1. プロバイダー一覧取得
        ktcpClient.ktcpServerEntrypoints.providersRequestEntrypoint.access(ServerProviderListMsg(), ctx)?.execute()
            ?: run {
                logger.severe { "Failed to send provider list request for user $userSubject" }
                return@coroutineScope false
            }

        val providerListedMsg = receiveMsg(ctx) ?: return@coroutineScope false
        val providerListed = providerListedMsg.tryToProviderList()
            ?.unwrap {
                it.printStackTrace()
                return@coroutineScope false
            } ?: run {
            logger.severe { "Failed to parse provider_listed message" }
            return@coroutineScope false
        }

        // 2. 自分の ktclIssuer と一致するプロバイダーIDを取得
        val myProviderIds = providerListed.providers
            .filter { it.issuer == ktclIssuer }
            .map { it.id }
            .toSet()

        if (myProviderIds.isEmpty()) {
            logger.info { "No providers matched ktclIssuer: $ktclIssuer" }
            return@coroutineScope false
        }

        // 3. キュー一覧取得
        ktcpClient.ktcpServerEntrypoints.queueList.access(ServerQueueListMsg(), ctx)?.execute()
            ?: run {
                logger.severe { "Failed to send queue list request for user $userSubject" }
                return@coroutineScope false
            }

        val queueListedMsg = receiveMsg(ctx) ?: return@coroutineScope false
        val queueListed = queueListedMsg.tryToQueueListed()
            ?.unwrap {
                it.printStackTrace()
                return@coroutineScope false
            } ?: run {
            logger.severe { "Failed to parse queue_listed message" }
            return@coroutineScope false
        }

        // 4. 自分のプロバイダーのキューのみ処理
        val myQueues = queueListed.queues.filter { it.providerId in myProviderIds }

        if (myQueues.isEmpty()) {
            logger.info { "No queues found for providers: $myProviderIds" }
            return@coroutineScope false
        }

        var executed = false
        for (queue in myQueues) {
            // 5. キューの設定 (setting) を取得
            ktcpClient.ktcpServerEntrypoints.queueShow.access(ServerQueueShowMsg(id = queue.id), ctx)?.execute()
                ?: run {
                    logger.severe { "Failed to send queue_show for queue ${queue.id}" }
                    continue
                }

            val queueShowedMsg = receiveMsg(ctx) ?: continue
            val queueShowed = queueShowedMsg.tryToQueueShowed()
                ?.unwrap {
                    it.printStackTrace()
                    null
                } ?: continue

            val gitRepoUrl = try {
                Json.parseToJsonElement(queueShowed.setting).jsonObject["git-repo"]?.jsonPrimitive?.content
            } catch (_: Exception) {
                null
            }
            if (gitRepoUrl == null) {
                logger.info { "git-repo not found in queue ${queue.id} setting, skipping" }
                continue
            }

            // 6. タスク一覧取得
            ktcpClient.ktcpServerEntrypoints.taskList.access(ServerTaskListMsg(queueId = queue.id), ctx)?.execute()
                ?: run {
                    logger.severe { "Failed to send task_list for queue ${queue.id}" }
                    continue
                }

            val taskListedMsg = receiveMsg(ctx) ?: continue
            val taskListed = taskListedMsg.tryToTaskListed()
                ?.unwrap {
                    it.printStackTrace()
                    null
                } ?: continue

            val task = taskListed.tasks.firstOrNull { it.status != "completed" } ?: continue

            val githubToken = userTokenDao.getGithubToken(userSubject, userIssuer) ?: run {
                logger.severe { "GitHub token not found for user $userSubject (issuer: $userIssuer), skipping queue ${queue.id}" }
                continue
            }

            // 7. K8s Job実行
            jobExecutor.executeJob(task.id, task.title, task.description, gitRepoUrl, githubToken)
                .unwrap {
                    it.printStackTrace()
                    null
                } ?: continue

            executed = true
        }

        return@coroutineScope executed
    }

    private suspend fun receiveMsg(ctx: ClientCtx): ReceiveClientUnknownArg? {
        val text = try {
            withTimeout(10.seconds) { connection.receive() }
        } catch (_: ClosedReceiveChannelException) {
            logger.info { "WebSocket connection closed" }
            return null
        } ?: return null
        return ReceiveClientUnknownArg.fromText(text, ctx.serializer)
    }
}
