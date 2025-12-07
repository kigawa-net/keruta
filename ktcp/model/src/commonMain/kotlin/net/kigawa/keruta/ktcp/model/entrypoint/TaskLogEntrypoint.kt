package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*




interface TaskLogEntrypoint : Entrypoint<TaskLogMessage, KtcpMessage> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("task_log", emptyList(), "タスクログ処理")

    override fun access(input: TaskLogMessage): KtcpMessage {
        // ログ処理
        return input // 通知なのでそのまま
    }
}
