package net.kigawa.keruta.ktcp.model.err.server.types

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class InvalidTypeDecodeFrameErr(message: String?, cause: DeserializeErr?): DecodeFrameErr(
    ServerErrCode.INVALID_TYPE_DECODE_FRAME, message, cause,
)
