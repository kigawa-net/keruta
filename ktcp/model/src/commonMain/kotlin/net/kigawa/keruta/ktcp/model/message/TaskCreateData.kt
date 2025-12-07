package net.kigawa.keruta.ktcp.model.message

// タスク作成データ
data class TaskCreateData(
    val name: String,
    val description: String,
    val timeout: Int,
    val tags: List<String>
)
