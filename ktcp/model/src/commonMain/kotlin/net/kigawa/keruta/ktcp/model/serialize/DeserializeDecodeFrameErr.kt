package net.kigawa.keruta.ktcp.model.serialize

import net.kigawa.keruta.ktcp.model.err.ErrCode

class DeserializeDecodeFrameErr(err: DeserializeErr): DecodeFrameErr(ErrCode.DESERIALIZE_DECODE_FRAME) {
}
