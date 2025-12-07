package net.kigawa.keruta.ktcp.model.message
// KTCP メッセージの基底インターフェース
interface KtcpMessage {
    val type: String
    val timestamp: String
}
