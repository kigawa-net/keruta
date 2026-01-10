package net.kigawa.keruta.ktcp.model.err.server.types

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class InvalidTypeDeserializeErr(message: String?, cause: IllegalArgumentException): DeserializeErr(
    ServerErrCode.INVALID_TYPE_DESERIALIZE, message, cause,
)
