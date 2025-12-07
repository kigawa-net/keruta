package net.kigawa.keruta.ktcp.model.message

// タスクキャンセルデータ
data class TaskCancelData(
    val reason: String,
    val force: Boolean
)
