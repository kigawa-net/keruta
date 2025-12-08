package net.kigawa.keruta.ktcp.model.message

// タスク実行要求メッセージ
data class TaskExecuteMessage(
    override val type: KtcpMessageType = KtcpMessageType.TASK_EXECUTE,
    val taskId: String,
    val data: TaskExecuteData,
    override val timestamp: String
) : KtcpMessage
