package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*




interface TaskUpdateResponseEntrypoint : Entrypoint<TaskUpdateResponseMessage, KtcpMessage> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("task_update_response", emptyList(), "タスク更新応答処理")

    override fun access(input: TaskUpdateResponseMessage): KtcpMessage {
        // 応答処理
        return input
    }
}
