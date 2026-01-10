package net.kigawa.keruta.ktcp.model.err.server.types

import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class DeserializeDecodeFrameErr(
    message: String?, cause: DeserializeErr,
): DecodeFrameErr(
    ServerErrCode.DESERIALIZE_DECODE_FRAME, message, cause
)
