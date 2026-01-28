package net.kigawa.keruta.ktcl.claudecode.task

import net.kigawa.keruta.ktcl.claudecode.claude.ClaudeCodeCliClient
import net.kigawa.keruta.ktcl.claudecode.err.ClaudeApiErr
import net.kigawa.keruta.ktcp.client.ClientCtx
import net.kigawa.keruta.ktcp.client.KtcpClient
import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.task.update.ServerTaskUpdateMsg
import net.kigawa.kodel.api.err.Res
import net.kigawa.kodel.api.log.LoggerFactory

class TaskExecutor(
    private val claudeClient: ClaudeCodeCliClient,
    private val ktcpClient: KtcpClient,
) {
    private val logger = LoggerFactory.get("TaskExecutor")

    suspend fun executeTask(
        taskId: Long,
        title: String,
        description: String,
        ctx: ClientCtx,
    ): Res<Unit, KtcpErr> {
        logger.info { "Executing task: id=$taskId, title=$title" }

        // ステータスを"running"に更新
        updateStatus(taskId, "running", ctx)

        // Claude APIでタスク実行
        val prompt = """
            タスクを実行してください:

            タイトル: $title
            説明: $description
        """.trimIndent()

        return when (val result = claudeClient.sendMessage(prompt)) {
            is Res.Ok -> {
                logger.info { "Task completed: id=$taskId" }
                updateStatus(taskId, "completed", ctx)
                Res.Ok(Unit)
            }
            is Res.Err -> {
                logger.info { "Task failed: id=$taskId" }
                updateStatus(taskId, "failed", ctx)
                Res.Err(ClaudeApiErr("Task execution failed", result.err as? Exception))
            }
        }
    }

    private suspend fun updateStatus(
        taskId: Long,
        status: String,
        ctx: ClientCtx,
    ): Res<Unit, KtcpErr> {
        return ktcpClient.ktcpServerEntrypoints.taskUpdateEntrypoint.access(
            ServerTaskUpdateMsg(
                taskId = taskId,
                status = status
            ),
            ctx
        )?.execute() ?: Res.Err(ClaudeApiErr("TaskUpdate entrypoint not found", null))
    }
}