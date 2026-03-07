package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.domain.err.server.ServerErrCode

class UnauthenticatedErr(message: String?, cause: Exception?): KtcpServerErr(
    ServerErrCode.UNAUTHENTICATED, message, cause
)
