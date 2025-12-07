package net.kigawa.keruta.ktcp.model.entrypoint

import net.kigawa.kodel.api.entrypoint.Entrypoint
import net.kigawa.kodel.api.entrypoint.EntrypointInfo
import net.kigawa.keruta.ktcp.model.message.*




interface TaskCompletedEntrypoint : Entrypoint<TaskCompletedMessage, KtcpMessage> {
    override val info: EntrypointInfo
        get() = EntrypointInfo("task_completed", emptyList(), "タスク完了通知処理")

    override fun access(input: TaskCompletedMessage): KtcpMessage {
        // 完了処理
        return input // 通知なのでそのまま
    }
}
