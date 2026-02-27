package net.kigawa.keruta.ktcp.model.err

class InvalidTypeDeserializeErr(message: String?, cause: IllegalArgumentException): KtcpErr(
    message, cause,
) {
    override val code: String
        get() = CommonErrCode.INVALID_TYPE_DESERIALIZE.name
}
