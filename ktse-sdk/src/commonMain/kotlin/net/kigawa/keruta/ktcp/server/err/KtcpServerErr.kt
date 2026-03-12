package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.domain.err.KtcpErr
import net.kigawa.keruta.ktcp.domain.err.server.ServerErrCode

open class KtcpServerErr(
     val errCode: ServerErrCode, message: String?, cause: Exception?,
): KtcpErr("$errCode: ${message ?: ""}", cause, ) {
    override val code: String
        get() = errCode.name
}
