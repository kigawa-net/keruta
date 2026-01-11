package net.kigawa.keruta.ktcp.server.err

import net.kigawa.keruta.ktcp.model.err.KtcpErr
import net.kigawa.keruta.ktcp.model.err.server.ServerErrCode

class DeserializeDecodeFrameErr(
    message: String?, cause: KtcpErr,
): DecodeFrameErr(
    ServerErrCode.DESERIALIZE_DECODE_FRAME, message, cause
)
