package net.kigawa.keruta.ktcp.model.message

// タスク実行要求メッセージ
data class TaskExecuteMessage(
    override val type: String = "task_execute",
    val taskId: String,
    val data: TaskExecuteData,
    override val timestamp: String
) : KtcpMessage
