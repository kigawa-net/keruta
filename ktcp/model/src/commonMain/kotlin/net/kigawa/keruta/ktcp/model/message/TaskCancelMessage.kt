package net.kigawa.keruta.ktcp.model.message
// タスクキャンセル要求メッセージ
data class TaskCancelMessage(
    val taskId: String,
    val data: TaskCancelData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_CANCEL
}
