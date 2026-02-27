package net.kigawa.keruta.ktcp.model.err

class EntrypointNotFoundErr(message: String, cause: Exception?): KtcpErr(
    message, cause,
) {
    override val code: String
        get() = CommonErrCode.ENTRYPOINT_NOT_FOUND.name
}
