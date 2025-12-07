package net.kigawa.keruta.ktcp.model.message
// タスク更新応答メッセージ
data class TaskUpdateResponseMessage(
    val requestId: String,
    val taskId: String,
    val data: TaskUpdateResponseData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_UPDATE_RESPONSE
}
