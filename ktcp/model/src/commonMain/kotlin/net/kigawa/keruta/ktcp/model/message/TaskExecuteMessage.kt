package net.kigawa.keruta.ktcp.model.message

// タスク実行要求メッセージ
data class TaskExecuteMessage(
    val taskId: String,
    val data: TaskExecuteData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_EXECUTE
}
