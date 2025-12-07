package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*




class TaskDeleteEntrypoint : Entrypoint<TaskDeleteMessage, KtcpMessage> {
    override val info = EntrypointInfo("task_delete", emptyList(), "タスク削除要求処理")

    override fun access(input: TaskDeleteMessage): KtcpMessage {
        // タスク削除処理
        return TaskDeleteResponseMessage(
            requestId = input.requestId,
            taskId = input.taskId,
            data = TaskDeleteResponseData(
                status = "DELETED",
                deletedAt = input.timestamp,
                cleanupStatus = "COMPLETED"
            ),
            timestamp = input.timestamp
        )
    }
}
