package net.kigawa.keruta.ktcp.model.err.types

import net.kigawa.keruta.ktcp.model.err.ErrCode

open class VerifyErr(
    code: ErrCode, cause: Exception? = null, message: String? = null,
): KtcpErr(
    code, cause = cause,
    message = message
)
