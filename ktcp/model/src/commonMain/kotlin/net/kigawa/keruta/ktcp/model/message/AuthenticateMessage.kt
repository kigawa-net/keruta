package net.kigawa.keruta.ktcp.model.message
// 認証メッセージ
data class AuthenticateMessage(
    val token: String,
    val clientType: String,
    val clientVersion: String,
    val capabilities: List<String>,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.AUTHENTICATE
}
