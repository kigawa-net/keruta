package net.kigawa.keruta.ktcp.model.message



// タスク削除データ
data class TaskDeleteData(
    val reason: String,
    val force: Boolean
)
