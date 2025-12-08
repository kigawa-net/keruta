package net.kigawa.keruta.ktcp.model.message

// タスク一覧取得応答データ
data class TaskListResponseData(
    val tasks: List<TaskSummary>,
    val totalCount: Int,
    val hasMore: Boolean
)
