package net.kigawa.keruta.ktcp.model.message

// タスク情報取得データ
data class TaskReadData(
    val includeLogs: Boolean,
    val includeMetadata: Boolean
)
