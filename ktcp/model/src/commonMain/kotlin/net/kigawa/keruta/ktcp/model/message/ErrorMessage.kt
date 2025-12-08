package net.kigawa.keruta.ktcp.model.message

// エラーメッセージ
data class ErrorMessage(
    val code: String,
    val message: String,
    val retryable: Boolean,
    override val timestamp: String
) : KtcpMessage{
    override val type: KtcpMessageType = KtcpMessageType.ERROR
}
