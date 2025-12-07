package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*






class TaskReadResponseEntrypoint : Entrypoint<TaskReadResponseMessage, KtcpMessage> {
    override val info = EntrypointInfo("task_read_response", emptyList(), "タスク情報取得応答処理")

    override fun access(input: TaskReadResponseMessage): KtcpMessage {
        // 応答処理
        return input
    }
}
