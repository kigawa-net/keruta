package net.kigawa.keruta.ktcp.model.message
// エラーメッセージ
data class TaskErrorMessage(
    override val type: KtcpMessageType = KtcpMessageType.TASK_ERROR,
    val taskId: String,
    val data: TaskErrorData,
    override val timestamp: String
) : KtcpMessage
