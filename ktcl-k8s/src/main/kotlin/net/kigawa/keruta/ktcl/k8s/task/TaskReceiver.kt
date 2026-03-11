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
import net.kigawa.keruta.ktcp.domain.queue.listed.ClientQueueListedMsg
import net.kigawa.keruta.ktcp.domain.queue.show.ServerQueueShowMsg
import net.kigawa.keruta.ktcp.domain.task.list.ServerTaskListMsg
import net.kigawa.kodel.api.err.unwrap
import net.kigawa.kodel.api.log.getKogger
import net.kigawa.kodel.api.log.traceignore.debug
import kotlin.time.Duration.Companion.seconds

class TaskReceiver(
    private val connection: JvmWebSocketConnection,
    private val ktcpClient: KtcpClient,
    private val jobExecutor: K8sJobExecutor,
    private val ktclIssuer: String,
    private val userTokenDao: UserTokenDao,
) {
    private val logger = getKogger()

    suspend fun startReceiving(ctx: ClientCtx, userSubject: String, userIssuer: String): Boolean = coroutineScope {
        if (!waitForAuthSuccess(ctx, userSubject)) return@coroutineScope false

        val myProviderIds = fetchMatchingProviderIds(ctx, userSubject) ?: return@coroutineScope false
        if (myProviderIds.isEmpty()) {
            logger.info { "No providers matched ktclIssuer: $ktclIssuer" }
            return@coroutineScope false
        }

        val myQueues = fetchMyQueues(ctx, userSubject, myProviderIds) ?: return@coroutineScope false
        if (myQueues.isEmpty()) {
            logger.info { "No queues found for providers: $myProviderIds" }
            return@coroutineScope false
        }

        myQueues.map { processQueue(ctx, userSubject, userIssuer, it) }.any { it }
    }

    private suspend fun waitForAuthSuccess(ctx: ClientCtx, userSubject: String): Boolean {
        val authMsg = receiveMsg(ctx) ?: return false
        if (authMsg.tryToAuthSuccess() == null) {
            logger.severe { "Expected auth_success but got different message for user $userSubject" }
            return false
        }
        return true
    }

    private suspend fun fetchMatchingProviderIds(ctx: ClientCtx, userSubject: String): Set<Long>? {
        ktcpClient.ktcpServerEntrypoints.providersRequestEntrypoint.access(ServerProviderListMsg(), ctx)?.execute()
            ?: run {
                logger.severe { "Failed to send provider list request for user $userSubject" }
                return null
            }

        val providerListedMsg = receiveMsg(ctx) ?: return null
        val providerListed = providerListedMsg.tryToProviderList()
            ?.unwrap { it.printStackTrace(); return null }
            ?: run {
                logger.severe { "Failed to parse provider_listed message" }
                return null
            }

        return providerListed.providers.filter { it.issuer == ktclIssuer }.map { it.id }.toSet()
    }

    private suspend fun fetchMyQueues(
        ctx: ClientCtx,
        userSubject: String,
        myProviderIds: Set<Long>,
    ): List<ClientQueueListedMsg.Queue>? {
        ktcpClient.ktcpServerEntrypoints.queueList.access(ServerQueueListMsg(), ctx)?.execute()
            ?: run {
                logger.severe { "Failed to send queue list request for user $userSubject" }
                return null
            }

        val queueListedMsg = receiveMsg(ctx) ?: return null
        val queueListed = queueListedMsg.tryToQueueListed()
            ?.unwrap { it.printStackTrace(); return null }
            ?: run {
                logger.severe { "Failed to parse queue_listed message" }
                return null
            }

        return queueListed.queues.filter { it.providerId in myProviderIds }
    }

    private suspend fun processQueue(
        ctx: ClientCtx,
        userSubject: String,
        userIssuer: String,
        queue: ClientQueueListedMsg.Queue,
    ): Boolean {
        ktcpClient.ktcpServerEntrypoints.queueShow.access(ServerQueueShowMsg(id = queue.id), ctx)?.execute()
            ?: run {
                logger.severe { "Failed to send queue_show for queue ${queue.id}" }
                return false
            }

        val queueShowedMsg = receiveMsg(ctx) ?: return false
        val queueShowed = queueShowedMsg.tryToQueueShowed()
            ?.unwrap { it.printStackTrace(); return false }
            ?: return false

        val gitRepoUrl = try {
            Json.parseToJsonElement(queueShowed.setting).jsonObject["git-repo"]?.jsonPrimitive?.content
        } catch (_: Exception) {
            null
        }
        if (gitRepoUrl == null) {
            logger.info { "git-repo not found in queue ${queue.id} setting, skipping" }
            return false
        }

        ktcpClient.ktcpServerEntrypoints.taskList.access(ServerTaskListMsg(queueId = queue.id), ctx)?.execute()
            ?: run {
                logger.severe { "Failed to send task_list for queue ${queue.id}" }
                return false
            }

        val taskListedMsg = receiveMsg(ctx) ?: return false
        val taskListed = taskListedMsg.tryToTaskListed()
            ?.unwrap { it.printStackTrace(); return false }
            ?: return false

        val task = taskListed.tasks.firstOrNull { it.status != "completed" } ?: return false

        val githubToken = userTokenDao.getGithubToken(userSubject, userIssuer) ?: run {
            logger.severe { "GitHub token not found for user $userSubject (issuer: $userIssuer), skipping queue ${queue.id}" }
            return false
        }
        logger.debug { "Executing task ${task.id} for queue ${queue.id}" }

        jobExecutor.executeJob(task.id, task.title, task.description, gitRepoUrl, githubToken)
            .unwrap { it.printStackTrace(); return false }
        logger.debug { "Task ${task.id} executed for queue ${queue.id}" }
        return true
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