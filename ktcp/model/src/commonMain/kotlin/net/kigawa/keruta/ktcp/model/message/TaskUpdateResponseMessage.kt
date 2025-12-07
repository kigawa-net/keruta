package net.kigawa.keruta.ktcp.model.message
// タスク更新応答メッセージ
data class TaskUpdateResponseMessage(
    override val type: String = "task_update_response",
    val requestId: String,
    val taskId: String,
    val data: TaskUpdateResponseData,
    override val timestamp: String
) : KtcpMessage
