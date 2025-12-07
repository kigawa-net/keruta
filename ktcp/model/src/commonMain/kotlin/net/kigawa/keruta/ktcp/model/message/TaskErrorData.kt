package net.kigawa.keruta.ktcp.model.message
// タスクエラーデータ
data class TaskErrorData(
    val status: String,
    val errorCode: String,
    val errorMessage: String,
    val exitCode: Int,
    val retryable: Boolean,
    val failedAt: String
)
