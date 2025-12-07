package net.kigawa.keruta.ktcp.model.message
// タスク情報取得要求メッセージ
data class TaskReadMessage(
    override val type: String = "task_read",
    val requestId: String,
    val taskId: String,
    val data: TaskReadData,
    override val timestamp: String
) : KtcpMessage
