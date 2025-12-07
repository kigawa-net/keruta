package net.kigawa.keruta.ktcp.model.message

// タスク削除応答データ
data class TaskDeleteResponseData(
    val status: String,
    val deletedAt: String,
    val cleanupStatus: String
)
