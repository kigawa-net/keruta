package net.kigawa.keruta.ktcp.model.auth.jwt

import net.kigawa.keruta.ktcp.model.err.KtcpErr

open class VerifyErr(
    override val code: String, message: String?, cause: Exception?,
): KtcpErr(
    message, cause
)
