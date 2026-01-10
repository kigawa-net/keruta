package net.kigawa.keruta.ktcp.model.err.server.types

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class UnexpectedErr(message: String?, cause: Exception?): KtcpServerErr(
    ServerErrCode.UNEXPECTED, message, cause
)
