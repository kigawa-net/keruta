package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*





class TaskDeleteResponseEntrypoint : Entrypoint<TaskDeleteResponseMessage, KtcpMessage> {
    override val info = EntrypointInfo("task_delete_response", emptyList(), "タスク削除応答処理")

    override fun access(input: TaskDeleteResponseMessage): KtcpMessage {
        // 応答処理
        return input
    }
}
