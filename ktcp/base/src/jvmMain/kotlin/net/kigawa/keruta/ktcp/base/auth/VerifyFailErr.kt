package net.kigawa.keruta.ktcp.base.auth

import net.kigawa.keruta.ktcp.model.auth.jwt.VerifyErr

class VerifyFailErr(message: String?, cause: Exception?): VerifyErr(
    "VERIFY_FAIL", message, cause,
)
