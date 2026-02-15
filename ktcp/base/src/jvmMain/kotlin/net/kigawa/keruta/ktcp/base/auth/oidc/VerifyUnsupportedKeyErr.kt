package net.kigawa.keruta.ktcp.base.auth.oidc

import net.kigawa.keruta.ktcp.model.auth.jwt.VerifyErr

class VerifyUnsupportedKeyErr(
    message: String, cause: Exception?,
): VerifyErr(
    "VERIFY_UNSUPPORTED_KEY", "Unsupported public key: $message", cause,
)
