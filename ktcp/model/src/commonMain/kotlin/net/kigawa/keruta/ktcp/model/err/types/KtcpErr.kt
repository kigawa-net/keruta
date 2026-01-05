package net.kigawa.keruta.ktcp.model.err.types

import net.kigawa.keruta.ktcp.model.err.ErrCode

open class KtcpErr(
    val code: ErrCode,
    message: String? = null,
    cause: Exception? = null,
): Exception(
    "$code: ${message ?: ""}",
    cause
)
