package net.kigawa.keruta.ktcp.model.message

// タスク作成応答メッセージ
data class TaskCreateResponseMessage(
    override val type: String = "task_create_response",
    val requestId: String,
    val taskId: String,
    val data: TaskCreateResponseData,
    override val timestamp: String
) : KtcpMessage
