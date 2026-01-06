package net.kigawa.keruta.ktcp.model.err.types

import net.kigawa.keruta.ktcp.model.err.ErrCode

class DeserializeDecodeFrameErr(
    err: DeserializeErr,
): DecodeFrameErr(
    ErrCode.DESERIALIZE_DECODE_FRAME,
    cause = err
)
