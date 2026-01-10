package net.kigawa.keruta.ktcp.model.err.server.types

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

open class KtcpServerErr(
    val code: ServerErrCode, message: String?, cause: Exception?,
): KtcpErr("$code: ${message ?: ""}", cause, )
