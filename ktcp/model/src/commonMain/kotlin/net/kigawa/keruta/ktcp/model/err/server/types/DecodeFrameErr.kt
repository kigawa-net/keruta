package net.kigawa.keruta.ktcp.model.err.server.types

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

sealed class DecodeFrameErr(
    code: ServerErrCode, message: String?, cause: DeserializeErr?,
): KtcpServerErr(code, message, cause)
