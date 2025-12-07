package net.kigawa.keruta.ktcp.model.message

// ログデータ
data class LogData(
    val level: String,
    val message: String,
    val metadata: Map<String, Any>?
)
