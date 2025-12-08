package net.kigawa.keruta.ktcp.model.message

// タスク完了データ
data class TaskCompletedData(
    val status: String,
    val exitCode: Int,
    val completedAt: String,
    val resourceUsage: ResourceUsage?
)
