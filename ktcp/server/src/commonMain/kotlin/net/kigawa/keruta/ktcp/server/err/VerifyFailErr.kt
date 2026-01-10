package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode
import net.kigawa.keruta.ktcp.server.err.VerifyErr

class VerifyFailErr(message: String?, cause: Exception?): VerifyErr(
    ServerErrCode.VERIFY_FAIL, message, cause,
)
