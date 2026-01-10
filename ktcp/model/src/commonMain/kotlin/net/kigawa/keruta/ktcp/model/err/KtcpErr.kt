package net.kigawa.keruta.ktcp.model.err

open class KtcpErr(
    message: String?,
    cause: Exception?,
): Exception(
    message,
    cause
)
