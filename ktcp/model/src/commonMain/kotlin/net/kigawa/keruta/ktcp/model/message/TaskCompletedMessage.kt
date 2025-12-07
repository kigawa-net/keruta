package net.kigawa.keruta.ktcp.model.message

// タスク完了通知メッセージ
data class TaskCompletedMessage(
    val taskId: String,
    val data: TaskCompletedData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_COMPLETED
}
