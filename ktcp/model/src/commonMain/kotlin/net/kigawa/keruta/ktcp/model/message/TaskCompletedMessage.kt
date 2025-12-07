package net.kigawa.keruta.ktcp.model.message

// タスク完了通知メッセージ
data class TaskCompletedMessage(
    override val type: KtcpMessageType = KtcpMessageType.TASK_COMPLETED,
    val taskId: String,
    val data: TaskCompletedData,
    override val timestamp: String
) : KtcpMessage
