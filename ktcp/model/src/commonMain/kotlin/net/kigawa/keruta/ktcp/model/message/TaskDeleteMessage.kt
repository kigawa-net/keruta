package net.kigawa.keruta.ktcp.model.message


// タスク削除要求メッセージ
data class TaskDeleteMessage(
    val requestId: String,
    val taskId: String,
    val data: TaskDeleteData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_DELETE
}
