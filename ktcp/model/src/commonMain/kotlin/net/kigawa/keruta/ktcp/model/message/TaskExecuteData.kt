package net.kigawa.keruta.ktcp.model.message

// タスク実行データ
data class TaskExecuteData(
    val name: String,
    val timeout: Int
)
