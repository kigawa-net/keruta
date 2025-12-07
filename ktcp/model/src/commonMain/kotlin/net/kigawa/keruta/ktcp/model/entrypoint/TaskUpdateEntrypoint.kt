package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*








interface TaskUpdateEntrypoint : Entrypoint<TaskUpdateMessage, KtcpMessage> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("task_update", emptyList(), "タスク更新要求処理")

    override fun access(input: TaskUpdateMessage): KtcpMessage {
        // タスク更新処理
        return TaskUpdateResponseMessage(
            requestId = input.requestId,
            taskId = input.taskId,
            data = TaskUpdateResponseData(
                status = "UPDATED",
                updatedAt = input.timestamp,
                changes = null // 変更がない場合
            ),
            timestamp = input.timestamp
        )
    }
}
