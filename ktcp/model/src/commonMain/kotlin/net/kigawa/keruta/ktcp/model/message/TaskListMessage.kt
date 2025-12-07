package net.kigawa.keruta.ktcp.model.message

// タスク一覧取得要求メッセージ
data class TaskListMessage(
    override val type: String = "task_list",
    val requestId: String,
    val data: TaskListData,
    override val timestamp: String
) : KtcpMessage
