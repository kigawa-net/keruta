package net.kigawa.keruta.ktcp.model.message

// ログストリーミングメッセージ
data class TaskLogMessage(
    override val type: KtcpMessageType = KtcpMessageType.TASK_LOG,
    val taskId: String,
    val data: LogData,
    override val timestamp: String
) : KtcpMessage
