package net.kigawa.keruta.ktcp.model.message
// リソース使用量
data class ResourceUsage(
    val cpuTime: Double,
    val memoryPeak: String,
    val executionTime: Int
)
