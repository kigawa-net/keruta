package net.kigawa.keruta.ktcl.k8s.task

import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import net.kigawa.keruta.ktcl.k8s.err.K8sErr
import net.kigawa.keruta.ktcl.k8s.k8s.JobStatus
import net.kigawa.keruta.ktcl.k8s.k8s.K8sJobExecutor
import net.kigawa.keruta.ktcl.k8s.k8s.K8sJobWatcher
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.update.ServerTaskUpdateMsg
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class TaskExecutor(
    private val jobExecutor: K8sJobExecutor,
    private val jobWatcher: K8sJobWatcher,
    private val ktcpClient: KtcpClient,
) {
    private val logger = LoggerFactory.get("TaskExecutor")

    suspend fun executeTask(
        taskId: Long,
        title: String,
        description: String,
        ctx: ClientCtx,
    ): Res<Unit, KtcpErr> = coroutineScope {
        logger.info { "Executing task: id=$taskId, title=$title" }

        // 1. ステータスを"running"に更新
        updateStatus(taskId, "running", ctx)

        // 2. Kubernetes Jobを作成・実行
        val jobName = when (val result = jobExecutor.executeJob(taskId, title, description)) {
            is Res.Ok -> result.value
            is Res.Err -> {
                logger.info { "Failed to create Job for task $taskId" }
                updateStatus(taskId, "failed", ctx)
                return@coroutineScope Res.Err(result.err)
            }
        }

        // 3. Job実行を監視（非同期）
        launch {
            jobWatcher.watchJob(jobName) { status ->
                when (status) {
                    JobStatus.SUCCEEDED -> {
                        logger.info { "Task $taskId completed successfully" }
                        updateStatus(taskId, "completed", ctx)
                    }
                    JobStatus.FAILED, JobStatus.TIMEOUT -> {
                        logger.info { "Task $taskId failed with status: $status" }
                        updateStatus(taskId, "failed", ctx)
                    }
                    JobStatus.RUNNING -> { /* 継続監視 */ }
                }
            }
        }

        Res.Ok(Unit)
    }

    private suspend fun updateStatus(
        taskId: Long,
        status: String,
        ctx: ClientCtx,
    ): Res<Unit, KtcpErr> {
        return ktcpClient.ktcpServerEntrypoints.taskUpdateEntrypoint.access(
            ServerTaskUpdateMsg(taskId = taskId, status = status),
            ctx
        )?.execute() ?: Res.Err(K8sErr.K8sClientErr("TaskUpdate entrypoint not found", null))
    }
}
