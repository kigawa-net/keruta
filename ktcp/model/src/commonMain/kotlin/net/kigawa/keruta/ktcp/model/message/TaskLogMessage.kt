package net.kigawa.keruta.ktcp.model.message

// ログストリーミングメッセージ
data class TaskLogMessage(
    override val type: String = "task_log",
    val taskId: String,
    val data: LogData,
    override val timestamp: String
) : KtcpMessage
