package net.kigawa.keruta.ktcp.model.message

// タスク完了通知メッセージ
data class TaskCompletedMessage(
    override val type: String = "task_completed",
    val taskId: String,
    val data: TaskCompletedData,
    override val timestamp: String
) : KtcpMessage
