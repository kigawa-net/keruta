package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.domain.err.server.ServerErrCode

class UnexpectedErr(message: String?, cause: Exception?): KtcpServerErr(
    ServerErrCode.UNEXPECTED, message, cause
)
