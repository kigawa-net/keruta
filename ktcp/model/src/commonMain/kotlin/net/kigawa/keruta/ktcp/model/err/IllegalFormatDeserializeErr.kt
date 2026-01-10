package net.kigawa.keruta.ktcp.model.err

import kotlinx.serialization.SerializationException
import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class IllegalFormatDeserializeErr(message: String?, cause: SerializationException): KtcpErr(
     message, cause
) {
    override val code: String
        get() = CommonErrCode.ILLEGAL_FORMAT_DESERIALIZE.name
}
