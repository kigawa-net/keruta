package net.kigawa.keruta.ktcp.model.message

// タスク一覧取得応答メッセージ
data class TaskListResponseMessage(
    val requestId: String,
    val data: TaskListResponseData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_LIST_RESPONSE
}
