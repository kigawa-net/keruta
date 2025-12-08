package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*




class TaskListResponseEntrypoint : Entrypoint<TaskListResponseMessage, KtcpMessage> {
    override val info = EntrypointInfo("task_list_response", emptyList(), "タスク一覧取得応答処理")

    override fun access(input: TaskListResponseMessage): KtcpMessage {
        // 応答処理
        return input
    }
}
