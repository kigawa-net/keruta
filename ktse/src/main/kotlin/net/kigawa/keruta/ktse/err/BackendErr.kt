package net.kigawa.keruta.ktse.err

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode
import net.kigawa.keruta.ktcp.server.err.KtcpServerErr

class BackendErr(message: String?, cause: Exception?): KtcpServerErr(
    ServerErrCode.BACKEND, message, cause
) {
}
