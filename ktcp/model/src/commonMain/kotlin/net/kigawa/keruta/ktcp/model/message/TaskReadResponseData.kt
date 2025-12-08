package net.kigawa.keruta.ktcp.model.message




// タスク情報取得応答データ
data class TaskReadResponseData(
    val name: String,
    val description: String,
    val status: String,
    val timeout: Int,
    val tags: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val createdBy: String,
    val assignedProvider: String?,
    val logs: List<LogData>,
    val metadata: Map<String, Any>?
)
