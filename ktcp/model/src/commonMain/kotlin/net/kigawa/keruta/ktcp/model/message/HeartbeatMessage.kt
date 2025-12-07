package net.kigawa.keruta.ktcp.model.message
// ハートビートメッセージ
data class HeartbeatMessage(
    override val type: KtcpMessageType = KtcpMessageType.HEARTBEAT,
    val status: String,
    override val timestamp: String
) : KtcpMessage
