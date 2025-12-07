package net.kigawa.keruta.ktcp.model.message
// エラーメッセージ
data class TaskErrorMessage(
    override val type: String = "task_error",
    val taskId: String,
    val data: TaskErrorData,
    override val timestamp: String
) : KtcpMessage
