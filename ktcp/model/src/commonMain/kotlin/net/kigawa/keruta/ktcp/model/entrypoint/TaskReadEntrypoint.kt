package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*





interface TaskReadEntrypoint : Entrypoint<TaskReadMessage, KtcpMessage> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("task_read", emptyList(), "タスク情報取得要求処理")

    override fun access(input: TaskReadMessage): KtcpMessage {
        // タスク読み取り処理（モックデータ）
        return TaskReadResponseMessage(
            requestId = input.requestId,
            taskId = input.taskId,
            data = TaskReadResponseData(
                name = "Sample Task",
                description = "サンプルタスク",
                status = "PENDING",
                timeout = 3600,
                tags = listOf("sample"),
                createdAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-01T00:00:00Z",
                createdBy = "user123",
                assignedProvider = null,
                logs = emptyList(),
                metadata = null
            ),
            timestamp = input.timestamp
        )
    }
}
