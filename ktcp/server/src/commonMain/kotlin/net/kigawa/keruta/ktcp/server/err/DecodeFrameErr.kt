package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

sealed class DecodeFrameErr(
    code: ServerErrCode, message: String?, cause: KtcpErr?,
): KtcpServerErr(code, message, cause)
