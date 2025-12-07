 package net.kigawa.keruta.ktcp.model.message

 import kotlin.time.Instant

 // KTCP メッセージの基底インターフェース
interface KtcpMessage {
    fun getType(): KtcpMessageType
    val timestamp: Instant
}
