package net.kigawa.keruta.ktse.err

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode
import net.kigawa.keruta.ktcp.server.err.KtcpServerErr

class UnknownIssuerErr(message: String?, cause: Exception?): KtcpServerErr(
    ServerErrCode.UNKNOWN_ISSUER, message, cause
)
