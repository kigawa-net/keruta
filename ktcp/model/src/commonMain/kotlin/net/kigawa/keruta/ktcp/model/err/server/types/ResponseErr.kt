package net.kigawa.keruta.ktcp.model.err.server.types

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class ResponseErr(message: String?, cause: Exception?): KtcpServerErr(
    ServerErrCode.RESPONSE_ERR, message, cause
) {
}
