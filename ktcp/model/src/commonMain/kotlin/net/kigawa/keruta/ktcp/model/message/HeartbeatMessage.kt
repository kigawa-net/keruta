package net.kigawa.keruta.ktcp.model.message
// ハートビートメッセージ
data class HeartbeatMessage(
    override val type: String = "heartbeat",
    val status: String,
    override val timestamp: String
) : KtcpMessage
