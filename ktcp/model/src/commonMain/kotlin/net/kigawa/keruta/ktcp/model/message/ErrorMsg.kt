package net.kigawa.keruta.ktcp.model.message

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// エラーメッセージ
data class ErrorMsg @OptIn(ExperimentalTime::class) constructor(
    val code: String,
    val message: String,
    val retryable: Boolean,
    override val timestamp: Instant,
): KtcpMessage {
    override fun getType(): KtcpMessageType = KtcpMessageType.ERROR
}
