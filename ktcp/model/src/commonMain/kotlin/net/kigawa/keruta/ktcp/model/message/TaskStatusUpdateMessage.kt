package net.kigawa.keruta.ktcp.model.message

// タスク状態更新メッセージ
data class TaskStatusUpdateMessage(
    val taskId: String,
    val data: TaskStatusData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_STATUS_UPDATE
}
