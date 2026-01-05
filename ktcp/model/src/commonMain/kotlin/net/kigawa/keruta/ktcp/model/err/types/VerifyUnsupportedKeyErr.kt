package net.kigawa.keruta.ktcp.model.err.types

import net.kigawa.keruta.ktcp.model.err.ErrCode

class VerifyUnsupportedKeyErr(
    pubKey: String,
): VerifyErr(
    ErrCode.VERIFY_UNSUPPORTED_KEY,
    message = "Unsupported public key: $pubKey"
)
