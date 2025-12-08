package net.kigawa.keruta.ktcp.model.message
// 認証メッセージ
data class AuthenticateMessage(
    override val type: KtcpMessageType = KtcpMessageType.AUTHENTICATE,
    val token: String,
    val clientType: String,
    val clientVersion: String,
    val capabilities: List<String>,
    override val timestamp: String
) : KtcpMessage
