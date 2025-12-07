package net.kigawa.keruta.ktcp.model.message
// タスク一覧取得データ
data class TaskListData(
    val status: List<String>?,
    val tags: List<String>?,
    val createdBy: String?,
    val limit: Int,
    val offset: Int,
    val sortBy: String,
    val sortOrder: String
)
