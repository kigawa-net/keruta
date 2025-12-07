package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*





class TaskListEntrypoint : Entrypoint<TaskListMessage, KtcpMessage> {
    override val info = EntrypointInfo("task_list", emptyList(), "タスク一覧取得要求処理")

    override fun access(input: TaskListMessage): KtcpMessage {
        // タスク一覧取得処理（モックデータ）
        return TaskListResponseMessage(
            requestId = input.requestId,
            data = TaskListResponseData(
                tasks = listOf(
                    TaskSummary(
                        taskId = "task-1",
                        name = "Sample Task 1",
                        status = "PENDING",
                        createdAt = "2024-01-01T00:00:00Z",
                        tags = listOf("sample")
                    )
                ),
                totalCount = 1,
                hasMore = false
            ),
            timestamp = input.timestamp
        )
    }
}
