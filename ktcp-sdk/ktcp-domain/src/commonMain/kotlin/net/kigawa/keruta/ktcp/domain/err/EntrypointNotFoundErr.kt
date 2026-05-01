package net.kigawa.keruta.ktcp.domain.err

class EntrypointNotFoundErr(message: String, cause: Exception?): KtcpErr(
    message, cause,
) {
    override val code: String
        get() = CommonErrCode.ENTRYPOINT_NOT_FOUND.name
}
