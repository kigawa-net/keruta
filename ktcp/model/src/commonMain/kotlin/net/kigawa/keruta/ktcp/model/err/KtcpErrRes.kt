package net.kigawa.keruta.ktcp.model.err

import net.kigawa.keruta.ktcp.model.KtcpRes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class KtcpErrRes @OptIn(ExperimentalTime::class) constructor(
    val code: ErrCode, val message: String, val retryable: Boolean,
    val timestamp: Instant = kotlin.time.Clock.System.now(),
): KtcpRes {
}
