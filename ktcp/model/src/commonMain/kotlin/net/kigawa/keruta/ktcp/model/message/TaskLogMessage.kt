package net.kigawa.keruta.ktcp.model.message

// ログストリーミングメッセージ
data class TaskLogMessage(
    val taskId: String,
    val data: LogData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_LOG
}
