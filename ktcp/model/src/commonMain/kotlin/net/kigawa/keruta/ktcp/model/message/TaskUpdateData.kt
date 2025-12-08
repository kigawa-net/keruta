package net.kigawa.keruta.ktcp.model.message




// タスク更新データ
data class TaskUpdateData(
    val name: String?,
    val description: String?,
    val timeout: Int?,
    val tags: List<String>?
)
