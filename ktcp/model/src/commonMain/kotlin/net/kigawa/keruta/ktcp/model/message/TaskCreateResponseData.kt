package net.kigawa.keruta.ktcp.model.message

// タスク作成応答データ
data class TaskCreateResponseData(
    val status: String,
    val createdAt: String,
    val estimatedStartTime: String?
)
