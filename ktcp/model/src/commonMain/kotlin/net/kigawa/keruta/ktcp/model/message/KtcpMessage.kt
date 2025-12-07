 package net.kigawa.keruta.ktcp.model.message

// KTCP メッセージの基底インターフェース
interface KtcpMessage {
    fun getType(): KtcpMessageType
    val timestamp: String
}
