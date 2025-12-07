package net.kigawa.keruta.ktcp.model.message
// ハートビートメッセージ
data class HeartbeatMessage(
    val status: String,
    override val timestamp: String
) : KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.HEARTBEAT
}
