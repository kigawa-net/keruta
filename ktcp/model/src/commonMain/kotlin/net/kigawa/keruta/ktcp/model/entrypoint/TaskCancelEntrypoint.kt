package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*




interface TaskCancelEntrypoint : Entrypoint<TaskCancelMessage, KtcpMessage> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("task_cancel", emptyList(), "タスクキャンセル要求処理")

    override fun access(input: TaskCancelMessage): KtcpMessage {
        // キャンセル処理
        return TaskStatusUpdateMessage(
            taskId = input.taskId,
            data = TaskStatusData(
                status = "CANCELLED",
                progress = null,
                message = "タスクがキャンセルされました: ${input.data.reason}",
                startedAt = null
            ),
            timestamp = input.timestamp
        )
    }
}
