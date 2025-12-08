package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*





class TaskCreateResponseEntrypoint : Entrypoint<TaskCreateResponseMessage, KtcpMessage> {
    override val info = EntrypointInfo("task_create_response", emptyList(), "タスク作成応答処理")

    override fun access(input: TaskCreateResponseMessage): KtcpMessage {
        // 応答処理
        return input
    }
}
