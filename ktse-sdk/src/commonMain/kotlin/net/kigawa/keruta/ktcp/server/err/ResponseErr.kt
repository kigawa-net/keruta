package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.domain.err.server.ServerErrCode

class ResponseErr(message: String?, cause: Exception?): KtcpServerErr(
    ServerErrCode.RESPONSE_ERR, message, cause
) {
}
