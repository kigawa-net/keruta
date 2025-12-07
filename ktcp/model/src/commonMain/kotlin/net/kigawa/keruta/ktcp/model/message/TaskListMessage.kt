package net.kigawa.keruta.ktcp.model.message

// タスク一覧取得要求メッセージ
data class TaskListMessage(
    override val type: KtcpMessageType = KtcpMessageType.TASK_LIST,
    val requestId: String,
    val data: TaskListData,
    override val timestamp: String
) : KtcpMessage
