package net.kigawa.keruta.ktcp.domain.err

import kotlinx.serialization.SerializationException

class IllegalFormatDeserializeErr(message: String?, cause: SerializationException): KtcpErr(
     message, cause
) {
    override val code: String
        get() = CommonErrCode.ILLEGAL_FORMAT_DESERIALIZE.name
}
