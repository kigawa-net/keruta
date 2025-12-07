package net.kigawa.keruta.ktcp.model.message

// エラーメッセージ
data class ErrorMessage(
    override val type: String = "error",
    val code: String,
    val message: String,
    val retryable: Boolean,
    override val timestamp: String
) : KtcpMessage
