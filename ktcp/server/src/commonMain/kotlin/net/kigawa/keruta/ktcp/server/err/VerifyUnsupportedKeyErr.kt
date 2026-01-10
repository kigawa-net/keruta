package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class VerifyUnsupportedKeyErr(
    message: String, cause: Exception?,
): VerifyErr(
    ServerErrCode.VERIFY_UNSUPPORTED_KEY, "Unsupported public key: $message", cause,
)
