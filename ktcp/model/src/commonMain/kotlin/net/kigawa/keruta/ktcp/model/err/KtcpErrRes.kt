package net.kigawa.keruta.ktcp.model.err

import net.kigawa.keruta.ktcp.model.KtcpRes
import kotlin.time.Instant

data class KtcpErrRes(val code: String, val message: String, val retryable: Boolean, val timestamp: Instant): KtcpRes {
}
