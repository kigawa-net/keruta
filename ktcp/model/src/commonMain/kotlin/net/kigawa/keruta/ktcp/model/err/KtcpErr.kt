package net.kigawa.keruta.ktcp.model.err

open class KtcpErr(
    val code: ErrCode
): Exception() {
}
