package net.kigawa.keruta.ktcp.model.message
// タスクキャンセル要求メッセージ
data class TaskCancelMessage(
    override val type: String = "task_cancel",
    val taskId: String,
    val data: TaskCancelData,
    override val timestamp: String
) : KtcpMessage
