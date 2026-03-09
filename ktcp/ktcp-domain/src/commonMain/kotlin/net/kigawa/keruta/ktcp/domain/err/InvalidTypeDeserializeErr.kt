package net.kigawa.keruta.ktcp.domain.err

class InvalidTypeDeserializeErr(message: String?, cause: IllegalArgumentException): KtcpErr(
    message, cause,
) {
    override val code: String
        get() = CommonErrCode.INVALID_TYPE_DESERIALIZE.name
}
