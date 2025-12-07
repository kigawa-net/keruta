package net.kigawa.keruta.ktcp.model.message

// タスク作成要求メッセージ
data class TaskCreateMessage(
    val requestId: String,
    val data: TaskCreateData,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.TASK_CREATE
}
