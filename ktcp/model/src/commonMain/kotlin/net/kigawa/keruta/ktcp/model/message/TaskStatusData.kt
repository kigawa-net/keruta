package net.kigawa.keruta.ktcp.model.message

// タスク状態データ
data class TaskStatusData(
    val status: String,
    val progress: Int?,
    val message: String?,
    val startedAt: String?
)
