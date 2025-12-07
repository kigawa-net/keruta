package net.kigawa.keruta.ktcp.model.message

// タスク作成要求メッセージ
data class TaskCreateMessage(
    override val type: String = "task_create",
    val requestId: String,
    val data: TaskCreateData,
    override val timestamp: String
) : KtcpMessage
