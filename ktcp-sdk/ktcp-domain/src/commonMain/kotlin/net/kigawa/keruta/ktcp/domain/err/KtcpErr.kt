package net.kigawa.keruta.ktcp.domain.err

abstract class KtcpErr(
    message: String?,
    cause: Exception?,
): Exception(
    message,
    cause
) {
    abstract val code: String
}
