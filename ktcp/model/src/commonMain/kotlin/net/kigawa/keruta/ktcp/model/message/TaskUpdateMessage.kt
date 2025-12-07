package net.kigawa.keruta.ktcp.model.message



// タスク更新要求メッセージ
data class TaskUpdateMessage(
    val requestId: String,
    val taskId: String,
    val data: TaskUpdateData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_UPDATE
}
