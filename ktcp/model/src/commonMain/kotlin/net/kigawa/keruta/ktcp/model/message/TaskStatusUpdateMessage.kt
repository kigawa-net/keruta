package net.kigawa.keruta.ktcp.model.message

// タスク状態更新メッセージ
data class TaskStatusUpdateMessage(
    override val type: KtcpMessageType = KtcpMessageType.TASK_STATUS_UPDATE,
    val taskId: String,
    val data: TaskStatusData,
    override val timestamp: String
) : KtcpMessage
