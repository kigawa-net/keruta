package net.kigawa.keruta.ktcp.model.message


// タスク削除要求メッセージ
data class TaskDeleteMessage(
    override val type: String = "task_delete",
    val requestId: String,
    val taskId: String,
    val data: TaskDeleteData,
    override val timestamp: String
) : KtcpMessage
