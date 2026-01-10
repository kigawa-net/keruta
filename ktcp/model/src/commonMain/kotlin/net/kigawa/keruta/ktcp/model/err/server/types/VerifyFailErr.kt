package net.kigawa.keruta.ktcp.model.err.server.types

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class VerifyFailErr(message: String?, cause: Exception?): VerifyErr(
    ServerErrCode.VERIFY_FAIL, message, cause,
)
