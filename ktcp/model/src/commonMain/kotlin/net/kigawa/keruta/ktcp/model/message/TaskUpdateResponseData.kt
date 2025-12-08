package net.kigawa.keruta.ktcp.model.message

// タスク更新応答データ
data class TaskUpdateResponseData(
    val status: String,
    val updatedAt: String,
    val changes: Map<String, Change>?
)
