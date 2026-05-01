package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.domain.err.server.ServerErrCode

sealed class DeserializeErr(
    code: ServerErrCode, message: String?, cause: IllegalArgumentException?,
): KtcpServerErr(code, message, cause)
