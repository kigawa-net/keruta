package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

open class VerifyErr(
    code: ServerErrCode, message: String?, cause: Exception?,
): KtcpServerErr(
    code, message, cause
)
