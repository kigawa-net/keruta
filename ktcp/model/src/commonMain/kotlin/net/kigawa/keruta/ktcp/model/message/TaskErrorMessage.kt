package net.kigawa.keruta.ktcp.model.message
// エラーメッセージ
data class TaskErrorMessage(
    val taskId: String,
    val data: TaskErrorData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_ERROR
}
