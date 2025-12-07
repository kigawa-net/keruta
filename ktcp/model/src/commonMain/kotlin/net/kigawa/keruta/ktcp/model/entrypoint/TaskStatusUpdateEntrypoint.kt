package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*




interface TaskStatusUpdateEntrypoint : Entrypoint<TaskStatusUpdateMessage, KtcpMessage> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("task_status_update", emptyList(), "タスク状態更新処理")

    override fun access(input: TaskStatusUpdateMessage): KtcpMessage {
        // 状態更新処理
        return input // 通知なのでそのまま
    }
}
