package net.kigawa.keruta.ktcp.model.message

// タスク一覧取得要求メッセージ
data class TaskListMessage(
    val requestId: String,
    val data: TaskListData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_LIST
}
