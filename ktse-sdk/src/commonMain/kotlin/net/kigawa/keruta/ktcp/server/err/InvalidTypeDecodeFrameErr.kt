package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.domain.err.server.ServerErrCode

class InvalidTypeDecodeFrameErr(message: String?, cause: DeserializeErr?): DecodeFrameErr(
    ServerErrCode.INVALID_TYPE_DECODE_FRAME, message, cause,
)
