package net.kigawa.keruta.ktse.reader

import net.kigawa.keruta.ktcp.model.err.ErrCode
import net.kigawa.keruta.ktcp.model.serialize.DeserializeErr

class DeserializeDecodeFrameErr(err: DeserializeErr): DecodeFrameErr(ErrCode.DESERIALIZE_DECODE_FRAME) {
}
