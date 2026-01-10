package net.kigawa.keruta.ktcp.model.err.server.types

import kotlinx.serialization.SerializationException
import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class IllegalFormatDeserializeErr(message: String?, cause: SerializationException): DeserializeErr(
    ServerErrCode.ILLEGAL_FORMAT_DESERIALIZE, message, cause
)
