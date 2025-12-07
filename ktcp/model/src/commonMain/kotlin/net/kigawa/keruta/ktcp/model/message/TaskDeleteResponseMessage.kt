package net.kigawa.keruta.ktcp.model.message

// タスク削除応答メッセージ
data class TaskDeleteResponseMessage(
    val requestId: String,
    val taskId: String,
    val data: TaskDeleteResponseData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_DELETE_RESPONSE
}
