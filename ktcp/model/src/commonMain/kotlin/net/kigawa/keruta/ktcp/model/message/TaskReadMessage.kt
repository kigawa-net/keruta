package net.kigawa.keruta.ktcp.model.message
// タスク情報取得要求メッセージ
data class TaskReadMessage(
    val requestId: String,
    val taskId: String,
    val data: TaskReadData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_READ
}
