package net.kigawa.keruta.ktcp.model.message
// タスク情報取得要求メッセージ
data class TaskReadMessage(
    override val type: KtcpMessageType = KtcpMessageType.TASK_READ,
    val requestId: String,
    val taskId: String,
    val data: TaskReadData,
    override val timestamp: String
) : KtcpMessage
