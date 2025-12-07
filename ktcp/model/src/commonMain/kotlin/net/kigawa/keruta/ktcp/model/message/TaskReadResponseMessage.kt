package net.kigawa.keruta.ktcp.model.message

// タスク情報取得応答メッセージ
data class TaskReadResponseMessage(
    val requestId: String,
    val taskId: String,
    val data: TaskReadResponseData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_READ_RESPONSE
}
