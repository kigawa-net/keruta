package net.kigawa.keruta.ktcp.model.message

// タスク一覧取得応答メッセージ
data class TaskListResponseMessage(
    override val type: String = "task_list_response",
    val requestId: String,
    val data: TaskListResponseData,
    override val timestamp: String
) : KtcpMessage
