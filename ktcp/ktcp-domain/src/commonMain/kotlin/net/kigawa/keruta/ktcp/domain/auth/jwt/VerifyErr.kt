package net.kigawa.keruta.ktcp.domain.auth.jwt

import net.kigawa.keruta.ktcp.domain.err.KtcpErr

open class VerifyErr(
    override val code: String, message: String?, cause: Exception?,
): KtcpErr(
    message, cause
)
