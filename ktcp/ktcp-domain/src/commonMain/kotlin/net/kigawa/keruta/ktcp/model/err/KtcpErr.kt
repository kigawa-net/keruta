package net.kigawa.keruta.ktcp.model.err

abstract class KtcpErr(
    message: String?,
    cause: Exception?,
): Exception(
    message,
    cause
) {
    abstract val code: String
}
