package net.kigawa.keruta.ktcp.model.message

// タスク状態更新メッセージ
data class TaskStatusUpdateMessage(
    override val type: String = "task_status_update",
    val taskId: String,
    val data: TaskStatusData,
    override val timestamp: String
) : KtcpMessage
