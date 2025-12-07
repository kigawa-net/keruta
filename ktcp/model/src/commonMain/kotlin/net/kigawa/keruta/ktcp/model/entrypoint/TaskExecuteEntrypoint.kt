package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*




interface TaskExecuteEntrypoint : Entrypoint<TaskExecuteMessage, KtcpMessage> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("task_execute", emptyList(), "タスク実行要求処理")

    override fun access(input: TaskExecuteMessage): KtcpMessage {
        // タスク実行処理の実装
        // 実際の実行ロジックはプラットフォーム固有
        return TaskStatusUpdateMessage(
            taskId = input.taskId,
            data = TaskStatusData(
                status = "PROCESSING",
                progress = 0,
                message = "タスク実行を開始しました",
                startedAt = input.timestamp
            ),
            timestamp = input.timestamp
        )
    }
}
