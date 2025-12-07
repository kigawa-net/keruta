package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*





class TaskCreateEntrypoint : Entrypoint<TaskCreateMessage, KtcpMessage> {
    override val info = EntrypointInfo("task_create", emptyList(), "タスク作成要求処理")

    override fun access(input: TaskCreateMessage): KtcpMessage {
        // タスク作成処理
        val taskId = "task-${System.currentTimeMillis()}-${input.requestId.hashCode()}"
        return TaskCreateResponseMessage(
            requestId = input.requestId,
            taskId = taskId,
            data = TaskCreateResponseData(
                status = "CREATED",
                createdAt = input.timestamp,
                estimatedStartTime = null
            ),
            timestamp = input.timestamp
        )
    }
}
