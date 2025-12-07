package net.kigawa.keruta.ktcp.model.message
// タスクサマリー
data class TaskSummary(
    val taskId: String,
    val name: String,
    val status: String,
    val createdAt: String,
    val tags: List<String>
)
